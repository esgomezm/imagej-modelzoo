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

package net.imagej.modelzoo.consumer.commands.postprocessing;

import net.imagej.modelzoo.consumer.network.model.Model;
import net.imagej.modelzoo.consumer.network.model.OutputNode;

import java.util.HashMap;
import java.util.Map;

public class PredictionPostprocessing implements Runnable {

	private Model model;

	private final Map<String, Object> outputs = new HashMap<>();

	@Override
	public void run() {
		//TODO
		// (1) get postprocessing steps from model config
		// (2) run each postprocessing command with the input according to the config
		// (3) collect outputs of postprocessing
		model.getOutputNodes().forEach(OutputNode::makeDataFit);
		model.getOutputNodes().forEach(this::addOutput);
	}

	private void addOutput(OutputNode node) {
		outputs.put(node.getName(), node.getData());
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Map<String, Object> getOutputs() {
		return outputs;
	}
}
