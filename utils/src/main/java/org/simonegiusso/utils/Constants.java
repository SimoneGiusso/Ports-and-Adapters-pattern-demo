package org.simonegiusso.utils;

@SuppressWarnings("PublicInnerClass")
public enum Constants {
    ;

    public static final String DRIVEN_TIMESERIES_PROPERTY = "ports.driven.timeseries";
    public static final String TIMESERIES_SOURCE_PROPERTY = DRIVEN_TIMESERIES_PROPERTY + ".source";

    public enum TIMESERIES_SOURCES {
        ;
        public static final String POSTGRES = Technologies.POSTGRES;
        public static final String CASSANDRA = Technologies.CASSANDRA;
    }
}
