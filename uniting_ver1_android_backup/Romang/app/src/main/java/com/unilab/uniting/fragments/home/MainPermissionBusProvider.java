package com.unilab.uniting.fragments.home;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class MainPermissionBusProvider {
    public static final Bus bus = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance(){
        return bus;
    }

    public MainPermissionBusProvider() {

    }
}
