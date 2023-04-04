/*
 *  Copyright (C) <2022> <XiaoMoMi>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.momirealms.customnameplates.object.font;

public enum ThinASCIIWidth {

    A('A', 3), a('a', 3), B('B', 3), b('b', 3),
    C('C', 3), c('c', 3), D('D', 3), d('d', 3),
    E('E', 3), e('e', 3), F('F', 3), f('f', 2),
    G('G', 3), g('g', 3), H('H', 3), h('h', 3),
    I('I', 2), i('i', 2), J('J', 3), j('j', 2),
    K('K', 3), k('k', 3), L('L', 3), l('l', 2),
    M('M', 3), m('m', 3), N('N', 3), n('n', 3),
    O('O', 3), o('o', 3), P('P', 3), p('p', 3),
    Q('Q', 3), q('q', 3), R('R', 3), r('r', 3),
    S('S', 3), s('s', 3), T('T', 3), t('t', 2),
    U('U', 3), u('u', 3), V('V', 3), v('v', 3),
    W('W', 3), w('w', 3), X('X', 3), x('x', 3),
    Y('Y', 3), y('y', 3), Z('Z', 3), z('z', 3),
    NUM_1('1', 2), NUM_2('2', 3), NUM_3('3', 3), NUM_4('4', 3),
    NUM_5('5', 3), NUM_6('6', 3), NUM_7('7', 3), NUM_8('8', 3),
    NUM_9('9', 3), NUM_0('0', 3), EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 3),
    NUM_SIGN('#', 3), DOLLAR_SIGN('$', 3), PERCENT('%', 3), UP_ARROW('^', 3),
    AMPERSAND('&', 3), ASTERISK('*', 3), LEFT_PARENTHESIS('(', 2),
    RIGHT_PARENTHESIS(')', 2), MINUS('-', 3), UNDERSCORE('_', 3), PLUS_SIGN('+', 3),
    EQUALS_SIGN('=', 3), LEFT_CURL_BRACE('{', 1), RIGHT_CURL_BRACE('}', 1),
    LEFT_BRACKET('[', 2), RIGHT_BRACKET(']', 2), COLON(':', 1), SEMI_COLON(';', 1),
    DOUBLE_QUOTE('\"', 2), SINGLE_QUOTE('\'', 1), LEFT_ARROW('<', 2),
    RIGHT_ARROW('>', 2), QUESTION_MARK('?', 3), SLASH('/', 3),
    BACK_SLASH('\\', 3), LINE('|', 1), TILDE('~', 3), TICK('`', 1),
    PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3),
    IN_BETWEEN(' ', 3), DEFAULT('默', 8);

    private final char character;
    private final int width;

    ThinASCIIWidth(char character, int width) {
        this.character = character;
        this.width = width;
    }

    public char getCharacter() {
        return this.character;
    }

    public int getWidth() {
        return this.width;
    }
}
