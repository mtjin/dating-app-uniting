package com.unilab.uniting.activities.meeting;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class MeetingBusProvider {
    public static final Bus bus = new Bus(ThreadEnforcer.ANY);

    public static Bus getInstance(){
        return bus;
    }

    public MeetingBusProvider() {

    }
}
