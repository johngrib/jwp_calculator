import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcFactory {


    static CalcRule create(final String input) {

        if(input == null) {
            return  null;
        }

        final CalcRule[] rules = new CalcRule[] {
                new BasicCalc(input),
                new CustomCalc(input),
                new NoneCalc(input)
        };

        CalcRule rule = Arrays.stream(rules)
                .filter(r -> r.isMatchedType(input))
                .findFirst()
                .get()
                ;

        return rule;
    }
}

interface CalcRule {
    boolean isMatchedType(final String input);
    int calc();
}

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

class BasicCalc extends  CalcCommon {

    public BasicCalc(String inputSentence) {
        super(inputSentence);
        super.regex = "^\\d+(?:[,\\:]\\d+)*$|^$";
        super.delimeter = "\\:|\\,";
    }
}

class NoneCalc extends CalcCommon {

    public NoneCalc(String inputSentence) {
        super(inputSentence);
        super.regex = "^.*$";
    }
    @Override
    public int calc() {
       throw new RuntimeException("알 수 없는 규칙입니다.");
    }
}

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

    @Override
    String getDelimeter() {
        return Pattern.quote(this.delimeter);
    }

    @Override
    String getSource() {
        return this.source;
    }

    @Override
    boolean validate(int number) {
        return number >= 0;
    }
}
