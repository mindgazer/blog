package org.mindgazer.netty.callback;

/**
 * @author mindgazer
 * @date 2020/01/21
 */
public class ModuleB implements ModuleACallback {

    @Override
    public void taskDown(String arg) {

    }

    public void watch(ModuleA moduleA) {
        moduleA.registerCallback((arg) -> {
            System.out.println("job done, let's process next job");
        });
    }

}
