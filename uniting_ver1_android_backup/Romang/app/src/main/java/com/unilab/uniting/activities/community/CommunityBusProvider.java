package com.unilab.uniting.activities.community;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class CommunityBusProvider {
    public static final Bus bus = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance(){
        return bus;
    }

    public CommunityBusProvider() {

    }
}
