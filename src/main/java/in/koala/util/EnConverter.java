package in.koala.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;

@Component
public class EnConverter {
    private String[] arrStartSung = {
            "k", "K", "n", "d", "D", "r", "m", "b",
            "B", "s", "S", "a", "j", "J", "ch", "c",
            "t", "p", "h"
    };

    private String[] arrMiddleSung = {
            "a", "e", "ya", "ae", "eo", "e", "yeo", "e",
            "o", "wa", "wae", "oe", "yo", "u", "wo", "we",
            "wi", "yu", "eu", "ui", "i"
    };

    private String[] arrEndSung = {
            "k", "K", "ks", "n", "nj", "nh", "d", "l",
            "lg", "lm", "lb", "ls", "lt", "lp", "lh", "m",
            "b", "bs", "s", "ss", "ng", "j", "ch", "c", "t",
            "p", "h"

    };

    private String[] arrSingleSung = {
            "r", "R", "rt", "s", "sw", "sg", "e", "E", "f",
            "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q",
            "Q", "qt", "t", "T", "d", "w", "W", "c", "z", "x",
            "v", "g"
    };

    public String ktoe(String korean){
        StringBuilder ret = new StringBuilder();
        for(int i = 0; i < korean.length(); i++){
            char c = korean.charAt(i);

            //완성형
            if(c >= '가' && c <= '힣'){
                String nfd = Normalizer.normalize(
                        Character.toString(c), Normalizer.Form.NFD);
                ret.append(arrStartSung[nfd.codePointAt(0) - 0x1100]);
                ret.append(arrMiddleSung[nfd.codePointAt(1) - 0x1161]);
                if(nfd.length() > 2)
                    ret.append(arrEndSung[nfd.codePointAt(2) - 0x11A8]);
                continue;
            }
            //단일 자음
            if(c >= 'ㄱ' && c <= 'ㅎ'){
                ret.append(arrSingleSung[c - 'ㄱ']);
                continue;
            }
            //단일 모음
            if(c >= 'ㅏ' && c <= 'ㅣ'){
                ret.append(arrMiddleSung[c - 'ㅏ']);
                continue;
            }
            //나머지 문자
            ret.append(c);
        }
        return ret.toString();
    }
}
