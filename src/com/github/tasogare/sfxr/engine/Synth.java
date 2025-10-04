// Copyright (c) 2013, Florian DORMONT/Eiyeron Fulmicendii tasogare All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted
// provided that the following conditions are met:
//
// Redistributions of source code must retain the above copyright notice, this list of conditions
// and the following disclaimer. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
// CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
// EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package com.github.tasogare.sfxr.engine;

import java.util.random.RandomGenerator;

/**
 * @author Eiyeron, tasogare
 * @version 1.00
 *
 *          Synth is the SFXR++ Sound Synth. It's used to generate sound from
 *          presets and give Sound as results.
 */
public class Synth {

	private Preset preset;

	private boolean playingSample = false;
	private int phase;
	private double fperiod;
	private double fmaxperiod;
	private double fslide;
	private double fdslide;
	private int period;
	private double squareDuty;
	private double squareSlide;
	private int envStage;
	private int envTime;
	private int[] envLength = new int[3];
	private double envVol;
	private double fphase;
	private double fdphase;
	private int iphase;
	private double[] phaserBuffer = new double[1024];
	private int ipp;
	private double[] noiseBuffer = new double[32];
	private double fltp;
	private double fltdp;
	private double fltw;
	private double fltw_d;
	private double fltdmp;
	private double fltphp;
	private double flthp;
	private double flthp_d;
	private double vibPhase;
	private double vibSpeed;
	private double vibAmp;
	private int repTime;
	private int repLimit;
	private int arpTime;
	private int arpLimit;
	private double arpMod;

	/**
	 *
	 * @param preset don't synth directly.
	 * @param rng
	 */
	public Synth(Preset preset, RandomGenerator rng) {
		this.preset = preset;
		resetSample(rng, false);
		playingSample = true;
	}

	/**
	 * @return Sound of the Preset given
	 */
	public Sound createSound(RandomGenerator rng) {
		final int LENGTH = envLength[0] + envLength[1] + envLength[2];
		var bytes = new byte[LENGTH];
		for (int i = 0; i < bytes.length; i++) {
			double synth = synthSample(rng);
			bytes[i] = (byte) (synth * 127f);
		}
		return new Sound(bytes, 44100);
	}

	private void resetSample(RandomGenerator rng, boolean restart) {
		if (!restart) {
			phase = 0;
		}

		fperiod = 100.0 / (preset.getStartFrequency() * preset.getStartFrequency() + 0.001);
		period = (int) fperiod;
		fmaxperiod = 100.0 / (preset.getMinimalFrequency() * preset.getMinimalFrequency() + 0.001);
		fslide = 1.0 - Math.pow(preset.getPitchSlide(), 3.0) * 0.01;
		fdslide = -Math.pow(preset.getPitchDeltaSlide(), 3.0) * 0.000001;
		squareDuty = 0.5f - preset.getSquareDuty() * 0.5f;
		squareSlide = -preset.getSquareDutySlide() * 0.00005f;

		if (preset.getArpeggioDepth() >= 0.0f) {
			arpMod = 1.0 - Math.pow(preset.getArpeggioDepth(), 2.0) * 0.9;
		} else {
			arpMod = 1.0 + Math.pow(preset.getArpeggioDepth(), 2.0) * 10.0;
		}

		arpTime = 0;
		arpLimit = (int) (Math.pow(1.0f - preset.getArpeggioSpeed(), 2.0f) * 20000 + 32);
		if (preset.getArpeggioSpeed() == 1.0f) {
			arpLimit = 0;
		}

		if (!restart) {
			// reset filter
			fltp = 0.0f;
			fltdp = 0.0f;
			fltw = Math.pow(preset.getLowpassFilterCutoff(), 3.0f) * 0.1f;
			fltw_d = 1.0f + preset.getLowpassFilterCutoffSlide() * 0.0001f;
			fltdmp = 5.0f / (1.0f + Math.pow(preset.getLowpassFilterResonance(), 2.0f) * 20.0f) * (0.01f + fltw);
			if (fltdmp > 0.8f) {
				fltdmp = 0.8f;
			}
			fltphp = 0.0f;
			flthp = Math.pow(preset.getHighpassFilterCutoff(), 2.0f) * 0.1f;
			flthp_d = 1.0 + preset.getHighpassFilterCutoffSlide() * 0.0003f;

			// reset vibrato
			vibPhase = 0.0f;
			vibSpeed = Math.pow(preset.getVibratoSpeed(), 2.0f) * 0.01f;
			vibAmp = preset.getVibratoStrength() * 0.5f;

			// reset envelope
			envVol = 0.0f;
			envStage = 0;
			envTime = 0;
			envLength[0] = (int) (preset.getAttackTime() * preset.getAttackTime() * 100000.0f);
			envLength[1] = (int) (preset.getSustainTime() * preset.getSustainTime() * 100000.0f);
			envLength[2] = (int) (preset.getDecayTime() * preset.getDecayTime() * 100000.0f);

			fphase = Math.pow(preset.getPhaserOffset(), 2.0f) * 1020.0f;
			if (preset.getPhaserOffset() < 0.0f) {
				fphase = -fphase;
			}

			fdphase = Math.pow(preset.getPhaserSlide(), 2.0f) * 1.0f;

			if (preset.getPhaserSlide() < 0.0f) {
				fdphase = -fdphase;
			}

			iphase = Math.abs((int) fphase);
			ipp = 0;

			for (int i = 0; i < 1024; i++) {
				phaserBuffer[i] = 0.0f;
			}

			for (int i = 0; i < 32; i++) {
				noiseBuffer[i] = rng.nextDouble() * 2.0 - 1.0f;
			}

			repTime = 0;
			repLimit = (int) (Math.pow(1.0f - preset.getRepeatSpeed(), 2.0f) * 20000 + 32);
			if (preset.getRepeatSpeed() == 0.0f) {
				repLimit = 0;
			}
		}
	}

	private double synthSample(RandomGenerator rng) {
		if (!playingSample) {
			return 0.0;
		}

		repTime++;
		if (repLimit != 0 && repTime >= repLimit) {
			repTime = 0;
			resetSample(rng, true);
		}

		// frequency envelopes/arpeggios
		arpTime++;
		if (arpLimit != 0 && arpTime >= arpLimit) {
			arpLimit = 0;
			fperiod *= arpMod;
		}

		fslide += fdslide;
		fperiod *= fslide;
		if (fperiod > fmaxperiod) {
			fperiod = fmaxperiod;
			if (preset.getMinimalFrequency() > 0.0f) {
				playingSample = false;
			}
		}

		double rfperiod = fperiod;
		if (vibAmp > 0.0f) {
			vibPhase += vibSpeed;
			rfperiod = fperiod * (1.0 + Math.sin(vibPhase) * vibAmp);
		}

		period = (int) rfperiod;
		if (period < 8) {
			period = 8;
		}

		squareDuty += squareSlide;
		if (squareDuty < 0.0f) {
			squareDuty = 0.0f;
		}

		if (squareDuty > 0.5f) {
			squareDuty = 0.5f;
		}

		// volume envelope
		envTime++;
		if (envStage > envLength.length || envTime > envLength[envStage]) {
			envTime = 0;
			envStage++;
			if (envStage == 3) {
				playingSample = false;
			}
		}

		if (envStage == 0) {
			envVol = (double) envTime / envLength[0];
		}

		if (envStage == 1) {
			envVol = 1.0f + Math.pow(1.0f - (double) envTime / envLength[1], 1.0f) * 2.0f * preset.getSustainPunch();
		}

		if (envStage == 2) {
			envVol = 1.0f - (double) envTime / envLength[2];
		}

		// phaser step
		fphase += fdphase;
		iphase = Math.abs((int) fphase);
		if (iphase > 1023) {
			iphase = 1023;
		}

		if (flthp_d != 0.0f) {
			flthp *= flthp_d;
			if (flthp < 0.00001f) {
				flthp = 0.00001f;
			}
			if (flthp > 0.1f) {
				flthp = 0.1f;
			}
		}

		// 8x supersampling
		double ssample = 0.0f;
		for (int si = 0; si < 8; si++) {
			double sample = 0.0f;
			phase++;
			if (phase >= period) {
				phase %= period;
				if (preset.getWaveType() == WaveForm.NOISE) {
					for (int j = 0; j < 32; j++) {
						noiseBuffer[j] = rng.nextDouble() * 2.0 - 1.0f;
					}
				}
			}

			// base waveform
			double fp = (double) phase / period;
			sample = switch (preset.getWaveType()) {
			case SQUARE -> {
				if (fp < squareDuty) {
					yield 0.5f;
				} else {
					yield -0.5f;
				}
			}
			case SAWTOOTH -> {
				yield 1.0f - fp * 2;
			}
			case SINE -> {
				yield Math.sin(fp * 2 * Math.PI);
			}
			case NOISE -> {
				yield noiseBuffer[phase * 32 / period];
			}
			case TRIANGLE -> {
				yield Math.abs(1 - fp * 2) - 1;
			}
			case TAN -> {
				yield Math.tan(Math.PI * fp);
			}
			case WHISTLE -> {
				yield 0.75 * Math.sin(fp * 2 * Math.PI) + 0.25 * Math.sin(fp * 2 * 20 * Math.PI);
			}
			case BREAKER -> {
				yield Math.abs(1 - fp * fp * 2) - 1;
			}
			default -> throw new AssertionError();
			};

			// lp filter
			double pp = fltp;
			fltw *= fltw_d;
			if (fltw < 0.0f) {
				fltw = 0.0f;
			}

			if (fltw > 0.1f) {
				fltw = 0.1f;
			}

			if (preset.getLowpassFilterCutoff() != 1.0f) {
				fltdp += (sample - fltp) * fltw;
				fltdp -= fltdp * fltdmp;
			} else {
				fltp = sample;
				fltdp = 0.0f;
			}
			fltp += fltdp;

			// hp filter
			fltphp += fltp - pp;
			fltphp -= fltphp * flthp;
			sample = fltphp;

			// phaser
			phaserBuffer[ipp & 1023] = sample;
			sample += phaserBuffer[(ipp - iphase + 1024) & 1023];
			ipp = (ipp + 1) & 1023;

			// final accumulation and envelope application
			ssample += sample * envVol;
		}

		ssample = ssample / 8 * preset.getMasterVolume();

		ssample *= 2.0f * preset.getSoundVolume();

		if (ssample > 1.0f) {
			ssample = 1.0f;
		}

		if (ssample < -1.0f) {
			ssample = -1.0f;
		}
		return ssample;
	}
}
