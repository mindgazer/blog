package org.mindgazer.netty.callback;

/**
 * @author mindgazer
 * @date 2020/01/21
 */
public class ModuleA {

    private ModuleACallback callback;

    public void inboundEvent() {
        System.out.println("event incoming");

        // do something other jobs

        callback.taskDown("some msg");
    }

    public void registerCallback(ModuleACallback callback) {
        this.callback = callback;
    }

}
