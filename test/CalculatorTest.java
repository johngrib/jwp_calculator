import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class CalculatorTest {

    /*
    * 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우
        구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)

    * 앞의 기본 구분자(쉼표, 콜론)외에 커스텀 구분자를 지정할 수 있다.
        커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.
        예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.

    * 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw한다.
    */

    @Rule
    public ExpectedException expectedExcetption = ExpectedException.none();

    @Before
    public void prepare() {
    }

    @Test
    public void test_factory() {

        final String subject = "CalcFactory가 알맞은 CalcRule을 리턴하는지 테스트한다. ";

        assertTrue(subject + "10", CalcFactory.create("") instanceof BasicCalc);
        assertTrue(subject + "20", CalcFactory.create("1,2") instanceof BasicCalc);
        assertTrue(subject + "30", CalcFactory.create("1,2:3") instanceof BasicCalc);

        assertTrue(subject + "110", CalcFactory.create("//..\n1") instanceof CustomCalc);
        assertTrue(subject + "120", CalcFactory.create("//..\n1..2..3") instanceof CustomCalc);
        assertTrue(subject + "130", CalcFactory.create("//@\n1@2@3") instanceof CustomCalc);

        assertTrue(subject + "210", CalcFactory.create("asdf") instanceof NoneCalc);
    }

    @Test
    public void test_basic_calc() {
        {
            final String subject = "기본 구분자 문자열 계산 테스트 ";
            assertEquals(subject + "10", 1 + 2, CalcFactory.create("1,2").calc());
            assertEquals(subject + "20", 1 + 2 + 3, CalcFactory.create("1,2,3").calc());
            assertEquals(subject + "20", 1 + 2 + 3, CalcFactory.create("1,2:3").calc());
            assertEquals(subject + "20", 111 + 22222 + 333333, CalcFactory.create("111,22222:333333").calc());
        }
    }

    @Test
    public void testRuntimeError_10() throws Exception {
        expectedExcetption.expect(RuntimeException.class);
        CalcFactory.create("1,2:-3").calc();
    }

    @Test
    public void testRuntimeError_20() throws Exception {
        expectedExcetption.expect(RuntimeException.class);
        CalcFactory.create("1,-2:-3").calc();
    }

    @Test
    public void testRuntimeError_30() throws Exception {
        expectedExcetption.expect(RuntimeException.class);
        CalcFactory.create("-2").calc();
    }

    @Test
    public void testRuntimeError_110() throws Exception {
        expectedExcetption.expect(RuntimeException.class);
        CalcFactory.create("//..\n-20").calc();
    }


    @Test
    public void testRuntimeError_120() throws Exception {
        expectedExcetption.expect(RuntimeException.class);
        CalcFactory.create("//..\n2..30..-70").calc();
        CalcFactory.create("//..\n2..30.70").calc();
    }
    @Test
    public void test_custom_calc() {
        final String subject = "커스텀 구분자 문자열 계산 테스트 ";

        assertEquals(subject + "10", 1, CalcFactory.create("//..\n1").calc());
        assertEquals(subject + "20", 1 + 2 + 3, CalcFactory.create("//..\n1..2..3").calc());
        assertEquals(subject + "30", 1 + 2 + 3, CalcFactory.create("//@\n1@2@3").calc());
        assertEquals(subject + "30", 121 + 23452 + 342, CalcFactory.create("//@\n121@23452@342").calc());
    }

}
