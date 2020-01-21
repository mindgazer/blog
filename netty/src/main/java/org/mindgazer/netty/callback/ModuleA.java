package org.mindgazer.netty.callback;

/**
 * @author mindgazer
 * @date 2020/01/21
 */
public class ModuleA {

    public interface EventHandler {
        void handle(String event);
    }

    private EventHandler callback;

    public void inboundEvent() {
        System.out.println("event incoming");
        // some codes
        callback.handle("event data");
    }

    public void register(EventHandler callback) {
        this.callback = callback;
    }

}
