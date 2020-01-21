package org.mindgazer.netty.callback;

/**
 * @author mindgazer
 * @date 2020/01/21
 */
public class ModuleA {

    public interface ModuleACallback {
        void fireSomeOptions(String event);
    }

    private ModuleACallback callback;

    public void inboundEvent() {
        System.out.println("event incoming");
        // some codes
        callback.fireSomeOptions("some msg");
    }

    public void registerCallback(ModuleACallback callback) {
        this.callback = callback;
    }

}
