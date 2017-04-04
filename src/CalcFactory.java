import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcFactory {


    /**
     * 주어진 문자열이 어느 CalcRule 객체에 적합한지 판별하여, 해당 CalcRule 객체를 리턴한다.
     */
    static CalcRule create(final String input) {

        if(input == null) {
            return  new NoneCalc(input);
        }

        final CalcRule[] rules = new CalcRule[] {
                new BasicCalc(input),
                new CustomCalc(input),
                new NoneCalc(input)
        };

        final CalcRule rule = Arrays.stream(rules)
                .filter(r -> r.isMatchedType(input))
                .findFirst()
                .get()
                ;

        return rule;
    }
}

/**
 * 계산 규칙 객체 인터페이스
 */
interface CalcRule {
    /**
     * 주어진 문자열이 계산 규칙 객체가 지원하는 타입인지 검사한다.
     */
    boolean isMatchedType(final String input);

    /**
     * 값을 계산해 리턴한다.
     */
    int calc();
}

/**
 * 계산 규칙 공통
 */
abstract class CalcCommon implements CalcRule {

    final String inputSentence;
    String regex = "";
    String delimeter = "";

    public CalcCommon(String inputSentence) {
        this.inputSentence = inputSentence;
    }

    @Override
    public boolean isMatchedType(String input) {
        return input.matches(regex);
    }

    @Override
    public int calc() {

        final Integer[] numbers = Arrays.stream(getSource().split(getDelimeter()))
                .map(Integer::parseInt)
                .toArray(Integer[]::new)
                ;

        boolean valid = Arrays.stream(numbers).allMatch(this::validate);

        if(!valid) {
            throw new RuntimeException();
        }

        final int result = Arrays.stream(numbers)
                .reduce((x,y) -> x+y)
                .get()
                ;
        return result;
    }

    String getSource() {
        return this.inputSentence;
    }

    String getDelimeter() {
        return this.delimeter;
    }

    boolean validate(int number) {
        return number >= 0;
    }
}

/**
 * 기본 규칙은 , 와 : 를 delimeter 로 삼는다.
 */
class BasicCalc extends  CalcCommon {

    public BasicCalc(String inputSentence) {
        super(inputSentence);
        super.regex = "^\\d+(?:[,\\:]\\d+)*$|^$";
        super.delimeter = "\\:|\\,";
    }

    /**
     * 음수는 취급하지 않는다.
     */
    boolean validate(int number) {
        return number >= 0;
    }
}

/**
 * 잘못된 입력을 처리하는 계산 규칙
 */
class NoneCalc extends CalcCommon {

    public NoneCalc(String inputSentence) {
        super(inputSentence);
        super.regex = "^.*$";
    }

    @Override
    public int calc() {
       throw new RuntimeException("정의되지 않은 규칙입니다.");
    }
}

/**
 * 커스텀 구분자를 처리하는 계산 규칙
 */
class CustomCalc extends CalcCommon {

    private String source = null;

    public CustomCalc(String inputSentence) {
        super(inputSentence);
        super.regex = "^\\/\\/(.+)\n((?:\\d+)(?:\\1\\d+)*)$";
    }

    @Override
    public int calc() {
        final Matcher m = Pattern.compile(super.regex).matcher(inputSentence);

        if(!m.find()) {
            throw new RuntimeException();
        }
        this.delimeter = m.group(1);
        this.source = m.group(2);

        return super.calc();
    }

    /**
     * 커스텀 구분자는 regex quoting 처리
     */
    @Override
    String getDelimeter() {
        return Pattern.quote(this.delimeter);
    }

    @Override
    String getSource() {
        return this.source;
    }

    /**
     * 음수는 취급하지 않는다.
     */
    @Override
    boolean validate(int number) {
        return number >= 0;
    }
}
