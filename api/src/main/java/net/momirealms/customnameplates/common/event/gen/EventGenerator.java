/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.customnameplates.common.event.gen;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.momirealms.customnameplates.common.event.Cancellable;
import net.momirealms.customnameplates.common.event.NameplatesEvent;
import net.momirealms.customnameplates.common.event.Param;
import net.momirealms.customnameplates.common.plugin.NameplatesPlugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * A class for generating event class
 */
public class EventGenerator {

    /**
     * A loading cache of event types to {@link EventGenerator}s.
     */
    private static final Map<Class<? extends NameplatesEvent>, EventGenerator> CACHE = new HashMap<>();

    /**
     * Generate a {@link EventGenerator} for the given {@code event} type.
     *
     * @param event the event type
     * @return the generated class
     */
    public static EventGenerator generate(Class<? extends NameplatesEvent> event) {
        if (CACHE.containsKey(event)) {
            return CACHE.get(event);
        } else {
            try {
                var generator = new EventGenerator(event);
                CACHE.put(event, generator);
                return generator;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }

    /**
     * A method handle for the constructor of the event class.
     */
    private final MethodHandle constructor;

    /**
     * An array of {@link MethodHandle}s, which can set values for each of the properties in the event class.
     */
    private final MethodHandle[] setters;

    private EventGenerator(Class<? extends NameplatesEvent> eventClass) throws Throwable {
        // get a TypeDescription for the event class
        TypeDescription eventClassType = new TypeDescription.ForLoadedType(eventClass);

        // determine a generated class name of the event
        String eventClassSuffix = eventClass.getName().substring(NameplatesEvent.class.getPackage().getName().length());
        String packageWithName = EventGenerator.class.getName();
        String generatedClassName = packageWithName.substring(0, packageWithName.lastIndexOf('.')) + eventClassSuffix;

        DynamicType.Builder<AbstractNameplatesEvent> builder = new ByteBuddy(ClassFileVersion.JAVA_V17)
                // create a subclass of AbstractEvent
                .subclass(AbstractNameplatesEvent.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                // using the predetermined generated class name
                .name(generatedClassName)
                // implement the event interface
                .implement(eventClassType)
                // implement all methods annotated with Param by simply returning the value from the corresponding field with the same name
                .method(isAnnotatedWith(Param.class))
                    .intercept(FieldAccessor.of(NamedElement.WithRuntimeName::getInternalName))
                // implement NameplatesEvent#eventType by returning the event class type
                .method(named("eventType").and(returns(Class.class)).and(takesArguments(0)))
                    .intercept(FixedValue.value(eventClassType))
                // implement AbstractEvent#mh by calling & returning the value of MethodHandles.lookup()
                .method(named("mhl").and(returns(MethodHandles.Lookup.class)).and(takesArguments(0)))
                    .intercept(MethodCall.invoke(MethodHandles.class.getMethod("lookup")))
                // implement a toString method
                .withToString();

        if (Cancellable.class.isAssignableFrom(eventClass)) {
            builder = builder
                    .defineField("cancelled", boolean.class, Visibility.PRIVATE)
                    .method(named("cancelled").and(returns(boolean.class)).and(takesArguments(0)))
                        .intercept(FieldAccessor.ofField("cancelled"))
                    .method(named("cancelled").and(takesArguments(boolean.class)))
                        .intercept(FieldAccessor.ofField("cancelled"));
        }

        // get a sorted array of all methods on the event interface annotated with @Param
        Method[] properties = Arrays.stream(eventClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Param.class))
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Param.class).value()))
                .toArray(Method[]::new);

        // for each property, define a field on the generated class to hold the value
        for (Method method : properties) {
            builder = builder.defineField(method.getName(), method.getReturnType(), Visibility.PRIVATE);
        }

        // finish building, load the class, get a constructor
        Class<? extends AbstractNameplatesEvent> generatedClass = builder.make().load(EventGenerator.class.getClassLoader()).getLoaded();
        this.constructor = MethodHandles.publicLookup().in(generatedClass)
                .findConstructor(generatedClass, MethodType.methodType(void.class, NameplatesPlugin.class))
                .asType(MethodType.methodType(AbstractNameplatesEvent.class, NameplatesPlugin.class));

        // create a dummy instance of the generated class & get the method handle lookup instance
        MethodHandles.Lookup lookup = ((AbstractNameplatesEvent) this.constructor.invoke((Object) null)).mhl();

        // get 'setter' MethodHandles for each property
        this.setters = new MethodHandle[properties.length];
        for (int i = 0; i < properties.length; i++) {
            Method method = properties[i];
            this.setters[i] = lookup.findSetter(generatedClass, method.getName(), method.getReturnType())
                    .asType(MethodType.methodType(void.class, new Class[]{AbstractNameplatesEvent.class, Object.class}));
        }
    }

    /**
     * Creates a new instance of the event class.
     */
    public NameplatesEvent newInstance(NameplatesPlugin plugin, Object... properties) throws Throwable {
        if (properties.length != this.setters.length) {
            throw new IllegalStateException("Unexpected number of properties. given: " + properties.length + ", expected: " + this.setters.length);
        }

        // create a new instance of the event
        final AbstractNameplatesEvent event = (AbstractNameplatesEvent) this.constructor.invokeExact(plugin);

        // set the properties onto the event instance
        for (int i = 0; i < this.setters.length; i++) {
            MethodHandle setter = this.setters[i];
            Object value = properties[i];
            setter.invokeExact(event, value);
        }

        return event;
    }
}
