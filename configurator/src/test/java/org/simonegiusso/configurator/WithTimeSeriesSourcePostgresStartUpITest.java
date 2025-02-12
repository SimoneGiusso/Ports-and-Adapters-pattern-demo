package org.simonegiusso.configurator;

import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCES.POSTGRES;
import static org.simonegiusso.utils.Constants.TIMESERIES_SOURCE_PROPERTY;

import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = TIMESERIES_SOURCE_PROPERTY + "=" + POSTGRES)
class WithTimeSeriesSourcePostgresStartUpITest extends BaseSpringStartUpITest {}