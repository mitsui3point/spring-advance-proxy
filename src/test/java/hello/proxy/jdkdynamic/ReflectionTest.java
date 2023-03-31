package hello.proxy.jdkdynamic;

import hello.proxy.log.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

/**
 * 리플렉션
 * - 공통 로직1과 공통 로직2는 호출하는 메서드만 다르고 전체 코드 흐름이 완전히 같다.
 *   먼저 start 로그를 출력한다.
 *   어떤 메서드를 호출한다.
 *   메서드의 호출 결과를 로그로 출력한다.
 * - 여기서 공통 로직1과 공통 로직 2를 하나의 메서드로 뽑아서 합칠 수 있을까?
 * - 쉬워 보이지만 메서드로 뽑아서 공통화하는 것이 생각보다 어렵다. 왜냐하면 중간에 호출하는 메서드가 다르기 때문이다.
 * - 호출하는 메서드인 target.callA() , target.callB() 이 부분만 동적으로 처리할 수 있다면 문제를 해결할 수 있을 듯 하다.
 * - 이럴 때 사용하는 기술이 바로 리플렉션이다. 리플렉션은 클래스나 메서드의 메타정보를 사용해서 동적으로 호출하는 메서드를 변경할 수 있다.
 *
 * 리플렉션 적용
 * - 정적인 target.callA(), target.callB() 코드를 리플렉션을 사용해서 Method 라는 메타정보로 추상화했다.
 * - 덕분에 공통 로직을 만들수 있게 되었다.
 *
 * 리플렉션 주의
 * - 리플렉션을 사용하면 클래스와 메서드의 메타정보를 사용해서 애플리케이션을 동적으로 유연하게 만들수 있다.
 *   하지만, 리플렉션 기술은 런타임에 동작하기 때문에, 컴파일 시점에 오류를 잡을 수 없다.
 * - 예를 들어서 지금까지 살펴본 코드에서 getMethod("callA") 안에 들어가는 문자를 실수로 getMethod("callZ") 로 작성해도 컴파일 오류가 발생하지 않는다.
 * - 대신 해당 코드를 직접 실행하는 시점에 발생하는 오류인 런타임 오류가 발생한다.
 * - 가장 좋은 오류는 개발자가 즉시 확인할 수 있는 컴파일 오류이고, 가장 무서운 오류는 사용자가 직접 실행할 때 발생하는 런타임 오류이다.
 *
 * - 따라서 리플렉션은 일반적으로 사용하면 안된다.
 * - 지금까지 프로그래밍 언어가 발달하면서 타입 정보를 기반으로 컴파일 시점에 오류를 잡아준 덕분에 개발자가 편하게 살았는데, 리플렉션은 그것에 역행하는 방식이다.
 *
 * - 리플렉션은 프레임워크 개발이나 또는 매우 일반적인 공통 처리가 필요할 때 부분적으로 주의해서 사용해야 한다.
 *
 * **참고**
 * - 용어혼동 주의..
 * - 런타임 오류와 런타임 예외
 *      - 런타임 오류: 소스저장 컴파일시 오류 안남, 실제 실행시 잘못된 파라미터 입력 등 유저의 예측되지 못한 행동으로 인한 오류
 *      - 런타임 예외: 컴파일러가 체크하지 않는 예외(잡아서 처리 혹은 밖으로 던지는 코드를 생략할수 있다), DB 트랜잭션시 예외가 발생할 경우 트랜잭션 전체 롤백
 * - 컴파일 오류와 체크 예외
 *      - 컴파일 오류: 소스저장 컴파일시 오류 남
 *      - 체크 예외: 컴파일러가 체크하는 예외(잡아서 처리 혹은 밖으로 던지도록 명시해야 한다), DB 트랜잭션시 예외가 발생할 경우 트랜잭션 롤백 하지 않음
 */
@Slf4j
public class ReflectionTest extends LogAppenders {
    /**
     * callA(), callB() 직접호출
     */
    @Test
    void reflection0() {
        Hello target = new Hello();
        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA();//호출하는 메서드가 다름
        log.info("result={}", result1);
        //공통 로직1 종료
        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB();//호출하는 메서드가 다름
        log.info("result={}", result2);
        //공통 로직2 종료

        assertThat(getOrderedLogs()).contains(
                "[INFO] start",
                "[INFO] callA",
                "[INFO] result=A",
                "[INFO] start",
                "[INFO] callB",
                "[INFO] result=B");
    }

    /**
     * callA(), callB() 직접호출
     * -> Method 로 추상화
     */
    @Test
    void reflection1() throws Exception {//ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //클래스 메타 정보 획득
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        //callA 메서드 메타 정보
        Method methodCallA = classHello.getMethod("callA");//파라미터 이름만 바꿔주면 다른 메서드를 동적으로 바꿔줄 수 있다.
        Object result1 = methodCallA.invoke(target);//methodCallA를 호출하게 되는데, target 인스턴스의 methodCallA 를 호출한다.
        log.info("result1={}", result1);

        //callB 메서드 메타 정보
        Method methodCallB = classHello.getMethod("callB");//파라미터 이름만 바꿔주면 다른 메서드를 동적으로 바꿔줄 수 있다.
        Object result2 = methodCallB.invoke(target);//methodCallB를 호출하게 되는데, target 인스턴스의 methodCallB 를 호출한다.
        log.info("result2={}", result2);

        assertThat(getOrderedLogs()).contains(
                "[INFO] callA",
                "[INFO] result1=A",
                "[INFO] callB",
                "[INFO] result2=B"
        );
    }

    /**
     * callA(), callB() 직접호출
     * -> Method 로 추상화
     */
    @Test
    void reflection2() throws Exception {
        //클래스 메타 정보 획득
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        dynamicCall(classHello.getMethod("callA"), target);
        dynamicCall(classHello.getMethod("callB"), target);

        assertThat(getOrderedLogs()).contains(
                "[INFO] start",
                "[INFO] callA",
                "[INFO] result=A",
                "[INFO] start",
                "[INFO] callB",
                "[INFO] result=B"
        );
    }

    private void dynamicCall(Method methodCall, Object target) throws Exception {
        log.info("start");
        Object result = methodCall.invoke(target);
        log.info("result={}", result);
    }

    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }
        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}
