/*-
 * #%L
 * ImageJ ModelZoo Consumer
 * %%
 * Copyright (C) 2019 MPI-CBG
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.modelzoo.consumer.network.model.tensorflow;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.framework.TensorInfo;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TensorFlowRunner {

	/*
	 * runs graph on multiple input / output tensors
	 *
	 */
	public static List<Tensor<?>> executeGraph(final SavedModelBundle model,
	                                           final List<Tensor> inputs, final List<String> inputNames,
	                                           final List<String> outputNames) throws IllegalArgumentException
	{

//		System.out.println("input operation: " + opName(inputTensorInfo));
//		System.out.println("output operation: " + opName(outputTensorInfo));

		Session.Runner runner = model.session().runner();
		for (int i = 0; i < inputs.size(); i++) {
			runner.feed(inputNames.get(i), inputs.get(i));
		}
		for (String outputName : outputNames) {
			runner.fetch(outputName);
		}
		return runner.run();
	}

	/**
	 * The SignatureDef inputs and outputs contain names of the form
	 * {@code <operation_name>:<output_index>}, where for this model,
	 * {@code <output_index>} is always 0. This function trims the {@code :0}
	 * suffix to get the operation name.
	 */
	private static String opName(final TensorInfo t) {
		final String n = t.getName();
		if (n.endsWith(":0")) {
			return n.substring(0, n.lastIndexOf(":0"));
		}
		return n;
	}

}
