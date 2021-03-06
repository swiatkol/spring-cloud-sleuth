/*
 * Copyright 2016-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.benchmarks.jmh.mvc;

import jmh.mbr.junit5.Microbenchmark;
import org.junit.jupiter.api.Disabled;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import org.springframework.cloud.sleuth.benchmarks.jmh.ProcessLauncherState;
import org.springframework.cloud.sleuth.benchmarks.jmh.TracerImplementation;

@Measurement(iterations = 5)
@Warmup(iterations = 1)
@Fork(value = 2, warmups = 0)
@BenchmarkMode(Mode.AverageTime)
@Microbenchmark
@Disabled("Process doesn't stop")
public class StartupBenchmarkTests {

	@Benchmark
	public void withAnnotations(ApplicationState state) throws Exception {
		state.run();
	}

	@Benchmark
	public void withoutAnnotations(ApplicationState state) throws Exception {
		state.setExtraArgs("--spring.sleuth.annotation.enabled=false", state.tracerImplementation.property());
		state.run();
	}

	@Benchmark
	public void withoutAsync(ApplicationState state) throws Exception {
		state.setExtraArgs("--spring.sleuth.async.enabled=false", "--spring.sleuth.annotation.enabled=false", state.tracerImplementation.property());
		state.run();
	}

	@Benchmark
	public void withoutScheduled(ApplicationState state) throws Exception {
		state.setExtraArgs("--spring.sleuth.scheduled.enabled=false", "--spring.sleuth.async.enabled=false",
				"--spring.sleuth.annotation.enabled=false", state.tracerImplementation.property());
		state.run();
	}

	@Benchmark
	public void withoutWeb(ApplicationState state) throws Exception {
		state.setExtraArgs("--spring.sleuth.web.enabled=false", "--spring.sleuth.scheduled.enabled=false",
				"--spring.sleuth.async.enabled=false", "--spring.sleuth.annotation.enabled=false", state.tracerImplementation.property());
		state.run();
	}

	@State(Scope.Benchmark)
	public static class ApplicationState extends ProcessLauncherState {

		@Param
		private TracerImplementation tracerImplementation;

		public ApplicationState() {
			super("target", "--server.port=0");
		}

		@TearDown(Level.Iteration)
		public void stop() throws Exception {
			super.after();
		}

	}

}
