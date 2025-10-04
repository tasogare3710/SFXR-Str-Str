// Copyright (c) 2013, Florian DORMONT/Eiyeron Fulmicendii tasogare All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of source code must retain the above copyright notice,
//    this list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
package com.github.tasogare.sfxr.engine;

import java.util.random.RandomGenerator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

/**
 * @author Eiyeron, tasogare
 * @version 1.00
 */
public class Sound {

	/**
	 * Generates A sound directly from a chosen FX
	 * 
	 * @param fx FX chosen
	 * @return The sound generated, ready-to-use
	 */
	public static Sound fromFx(FX fx, RandomGenerator rng) {
		var preset = Preset.fromFx(rng, fx);
		var synth = new Synth(preset, rng);
		return synth.createSound(rng);
	}

	private byte[] pcm;

	private double[] pcmDouble;

	private int sampleRate;

	/**
	 * Doesn't verify if the Byte array equals Double array
	 * 
	 * @param pcm        Sound byte array
	 * @param pcmDouble  double array
	 * @param sampleRate Sound's sample rate
	 */
	public Sound(byte[] pcm, double[] pcmDouble, int sampleRate) {
		this.pcm = pcm;
		this.pcmDouble = pcmDouble;
		this.sampleRate = sampleRate;
	}

	/**
	 * @param pcm        Sound byte array
	 * @param sampleRate Sound's sample rate
	 */
	public Sound(byte[] pcm, int sampleRate) {
		this.pcm = pcm;
		convertByteArrayToDouble();
		this.sampleRate = sampleRate;

	}

	/**
	 * @param pcmDouble  Sound double array
	 * @param sampleRate Sound's sample rate
	 */
	public Sound(double[] pcmDouble, int sampleRate) {
		this.pcm = new byte[pcmDouble.length];
		this.pcmDouble = pcmDouble;
		convertDoubleArrayToByte();
		this.sampleRate = sampleRate;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Sound(pcm.clone(), sampleRate);
	}

	private void convertByteArrayToDouble() {
		this.pcmDouble = new double[pcm.length];
		for (int i = 0; i < pcm.length; i++) {
			pcmDouble[i] = (int) pcm[i] / 127.;
		}
	}

	private void convertDoubleArrayToByte() {
		this.pcm = new byte[pcm.length];
		for (int i = 0; i < pcm.length; i++) {
			pcm[i] = (byte) (pcm[i] * 127);
		}
	}

	public AudioFormat createAudioFormat(int sampleSizeInBits, int channels, int frameSize, boolean bigEndian) {
		return new AudioFormat(Encoding.PCM_SIGNED, getSampleRate(), sampleSizeInBits, channels, frameSize,
				getSampleRate(), bigEndian);
	}

	/**
	 * @return Sound's byte array
	 */
	public byte[] getPcm() {
		return pcm;
	}

	/**
	 * @return Sound's double array
	 */
	public double[] getPcmDouble() {
		return pcmDouble;
	}

	/**
	 * @return Sample rate
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * @param pcm Sound's byte array
	 */
	public void setPcm(byte[] pcm) {
		this.pcm = pcm;
		convertByteArrayToDouble();
	}

	/**
	 * @param pcmDouble Sound's double array
	 */
	public void setPcmDouble(double[] pcmDouble) {
		this.pcmDouble = pcmDouble;
		convertDoubleArrayToByte();
	}

	/**
	 * @param sampleRate sample rate
	 */
	public void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}
}
