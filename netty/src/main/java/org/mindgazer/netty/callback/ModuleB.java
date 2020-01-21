package org.mindgazer.netty.callback;

/**
 * @author mindgazer
 * @date 2020/01/21
 */
public class ModuleB {

    public void watch(ModuleA moduleA) {
        moduleA.register((event) -> {
            // do something after the event processed
        });
    }

}
