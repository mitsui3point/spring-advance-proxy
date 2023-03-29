package hello.proxy.pureproxy.proxy.code;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProxyPatternClient {
    private final Subject subject;
    public String execute() {
        return subject.operation();
    }
}
