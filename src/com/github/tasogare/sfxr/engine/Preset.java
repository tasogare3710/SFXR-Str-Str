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

/**
 * @author Eiyeron, tasogare
 * @version 1.00
 */
public class Preset implements Cloneable {

	private static int clampInt(double x, int a, int b) {
		int intX = (int) x;
		if (intX < a) {
			return a;
		} else if (intX > b) {
			return b;
		} else {
			return intX;
		}
	}

	private static double frnd(RandomGenerator rng, double v) {
		return rng.nextDouble() * v;
	}

	/**
	 * Generates a predefined preset.
	 *
	 * @param fx FX chosen
	 */
	public static Preset fromFx(RandomGenerator rng, FX fx) {
		var preset = new Preset();
		preset.random(rng, fx);
		return preset;
	}

	private static int rnd(RandomGenerator rng, int v) {
		return clampInt(Math.round(rng.nextDouble() * v), 0, v);
	}

	private static WaveForm rndToWaveForm(RandomGenerator rng, int v) {
		return WaveForm.values()[rnd(rng, v)];
	}

	private WaveForm waveType = WaveForm.SQUARE;
	/**
	 * Start Frequency
	 */
	private double startFrequency = 0;
	/**
	 * Minimal Frequency
	 */
	private double minimalFrequency = 0;

	/**
	 * Pitch Slide
	 */
	private double pitchSlide = 0;
	/**
	 * Pitch delta Slide
	 */
	private double pitchDeltaSlide = 0;

	/**
	 * Square Duty. Only used with square waveform
	 */
	private double squareDuty = 0.5;
	/**
	 * Square Duty Slide
	 */
	private double squareDutySlide = 0;
	/**
	 * Vibrato Depth
	 */
	private double vibratoStrength = 0;
	/**
	 * Vibrato Speed
	 */
	private double vibratoSpeed = 0;

	/**
	 * Attack Time
	 */
	private double attackTime = 0;
	/**
	 * Sustain Time
	 */
	private double sustainTime = 0;
	/**
	 * Decay Time
	 */
	private double decayTime = 0;
	/**
	 * Sustain Punch
	 */
	private double sustainPunch = 0;
	/**
	 * LP Filter Resonance
	 */
	private double lowpassFilterResonance = 0;

	/**
	 * LP Filter Cutoff
	 */
	private double lowpassFilterCutoff = 0;
	/**
	 * LP Filter Cutoff Slide
	 */
	private double lowpassFilterCutoffSlide = 0;

	/**
	 * HP Filter Cutoff
	 */
	private double highpassFilterCutoff;

	/**
	 * HP Filter Cutoff Slide
	 */
	private double highpassFilterCutoffSlide;
	/**
	 * Phaser Offset
	 */
	private double phaserOffset = 0;

	/**
	 * Phaser Slide
	 */
	private double phaserSlide = 0;

	/**
	 * Repeat Speed
	 */
	private double repeatSpeed = 0;

	/**
	 * Arpeggio Speed
	 */
	private double arpeggioSpeed = 0;

	/**
	 * Arpeggio Depth
	 */
	private double arpeggioDepth = 0;

	/***
	 * Master Volume
	 */
	private double masterVolume = 0.05f;

	/**
	 * Sound Volume
	 */
	private double soundVolume = 0.5f;

	/**
	 * Just do it manually. Please.
	 */
	public Preset() {
		resetParams();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		var other = new Preset();
		other.setArpeggioDepth(arpeggioDepth);
		other.setArpeggioSpeed(arpeggioSpeed);
		other.setAttackTime(attackTime);
		other.setDecayTime(decayTime);
		other.setHighpassFilterCutoff(highpassFilterCutoff);
		other.setHighpassFilterCutoffSlide(highpassFilterCutoffSlide);
		other.setLowpassFilterCutoff(lowpassFilterCutoff);
		other.setLowpassFilterCutoffSlide(lowpassFilterCutoffSlide);
		other.setLowpassFilterResonance(lowpassFilterResonance);
		other.setMasterVolume(masterVolume);
		other.setMinimalFrequency(minimalFrequency);
		other.setPhaserOffset(phaserOffset);
		other.setPhaserSlide(phaserSlide);
		other.setPitchDeltaSlide(pitchDeltaSlide);
		other.setPitchSlide(pitchSlide);
		other.setRepeatSpeed(repeatSpeed);
		other.setSoundVolume(soundVolume);
		other.setSquareDuty(squareDuty);
		other.setSquareDutySlide(squareDutySlide);
		other.setStartFrequency(startFrequency);
		other.setSustainPunch(sustainPunch);
		other.setSustainTime(sustainTime);
		other.setVibratoSpeed(vibratoSpeed);
		other.setVibratoStrength(vibratoStrength);
		other.setWaveType(waveType);
		return other;
	}

	/**
	 * @return the arpeggioDepth
	 */
	public double getArpeggioDepth() {
		return arpeggioDepth;
	}

	/**
	 * @return the arpeggioSpeed
	 */
	public double getArpeggioSpeed() {
		return arpeggioSpeed;
	}

	/**
	 * @return the attackTime
	 */
	public double getAttackTime() {
		return attackTime;
	}

	/**
	 * @return the decayTime
	 */
	public double getDecayTime() {
		return decayTime;
	}

	/**
	 * @return the highpassFilterCutoff
	 */
	public double getHighpassFilterCutoff() {
		return highpassFilterCutoff;
	}

	/**
	 * @return the highpassFilterCutoffSlide
	 */
	public double getHighpassFilterCutoffSlide() {
		return highpassFilterCutoffSlide;
	}

	/**
	 * @return the lowpassFilterCutoff
	 */
	public double getLowpassFilterCutoff() {
		return lowpassFilterCutoff;
	}

	/**
	 * @return the lowpassFilterCutoffSlide
	 */
	public double getLowpassFilterCutoffSlide() {
		return lowpassFilterCutoffSlide;
	}

	/**
	 * @return the lowpassFilterResonance
	 */
	public double getLowpassFilterResonance() {
		return lowpassFilterResonance;
	}

	/**
	 * @return the masterVolume
	 */
	public double getMasterVolume() {
		return masterVolume;
	}

	/**
	 * @return the minimalFrequency
	 */
	public double getMinimalFrequency() {
		return minimalFrequency;
	}

	/**
	 * @return the phaserOffset
	 */
	public double getPhaserOffset() {
		return phaserOffset;
	}

	/**
	 * @return the phaserSlide
	 */
	public double getPhaserSlide() {
		return phaserSlide;
	}

	/**
	 * @return the pitchDeltaSlide
	 */
	public double getPitchDeltaSlide() {
		return pitchDeltaSlide;
	}

	/**
	 * @return the pitchSlide
	 */
	public double getPitchSlide() {
		return pitchSlide;
	}

	/**
	 * @return the repeatSpeed
	 */
	public double getRepeatSpeed() {
		return repeatSpeed;
	}

	/**
	 * @return the soundVolume
	 */
	public double getSoundVolume() {
		return soundVolume;
	}

	/**
	 * @return the squareDuty
	 */
	public double getSquareDuty() {
		return squareDuty;
	}

	/**
	 * @return the squareDutySlide
	 */
	public double getSquareDutySlide() {
		return squareDutySlide;
	}

	/**
	 * @return the startFrequency
	 */
	public double getStartFrequency() {
		return startFrequency;
	}

	/**
	 * @return the sustainPunch
	 */
	public double getSustainPunch() {
		return sustainPunch;
	}

	/**
	 * @return the sustainTime
	 */
	public double getSustainTime() {
		return sustainTime;
	}

	/**
	 * @return the vibratoSpeed
	 */
	public double getVibratoSpeed() {
		return vibratoSpeed;
	}

	/**
	 * @return the vibratoStrength
	 */
	public double getVibratoStrength() {
		return vibratoStrength;
	}

	/**
	 * @return the waveType
	 */
	public WaveForm getWaveType() {
		return waveType;
	}

	/**
	 * Slightly alters the parameters. Useful to avoid sound repetitiveness
	 */
	public void mutate(RandomGenerator rng) {
		if (rnd(rng, 1) > 0) {
			startFrequency += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			pitchSlide += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			pitchDeltaSlide += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			squareDuty += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			squareDutySlide += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			vibratoStrength += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			vibratoSpeed += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			attackTime += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			sustainTime += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			decayTime += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			sustainPunch += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			lowpassFilterResonance += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			lowpassFilterCutoff += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			lowpassFilterCutoffSlide += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			highpassFilterCutoff += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			highpassFilterCutoffSlide += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			phaserOffset += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			phaserSlide += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			repeatSpeed += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			arpeggioSpeed += frnd(rng, 0.1f) - 0.05f;
		}

		if (rnd(rng, 1) > 0) {
			arpeggioDepth += frnd(rng, 0.1f) - 0.05f;
		}
	}

	/**
	 *
	 * @param rng
	 */
	public void random(RandomGenerator rng) {
		startFrequency = Math.pow(frnd(rng, 2.0f) - 1.0f, 2.0f);
		if (rnd(rng, 1) > 0) {
			startFrequency = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f) + 0.5f;
		}

		minimalFrequency = 0.0f;

		pitchSlide = Math.pow(frnd(rng, 2.0f) - 1.0f, 5.0f);
		if (startFrequency > 0.7f && pitchSlide > 0.2f) {
			pitchSlide = -pitchSlide;
		}

		if (startFrequency < 0.2f && pitchSlide < -0.05f) {
			pitchSlide = -pitchSlide;
		}

		pitchDeltaSlide = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f);
		squareDuty = frnd(rng, 2.0f) - 1.0f;
		squareDutySlide = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f);
		vibratoStrength = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f);
		vibratoSpeed = frnd(rng, 2.0f) - 1.0f;
		attackTime = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f);
		sustainTime = Math.pow(frnd(rng, 2.0f) - 1.0f, 2.0f);
		decayTime = frnd(rng, 2.0f) - 1.0f;
		sustainPunch = Math.pow(frnd(rng, 0.8f), 2.0f);
		if (attackTime + sustainTime + decayTime < 0.2f) {
			sustainTime += 0.2f + frnd(rng, 0.3f);
			decayTime += 0.2f + frnd(rng, 0.3f);
		}

		lowpassFilterResonance = frnd(rng, 2.0f) - 1.0f;
		lowpassFilterCutoff = 1.0f - Math.pow(frnd(rng, 1.0f), 3.0f);
		lowpassFilterCutoffSlide = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f);
		if (lowpassFilterCutoff < 0.1f && lowpassFilterCutoffSlide < -0.05f) {
			lowpassFilterCutoffSlide = -lowpassFilterCutoffSlide;
		}

		highpassFilterCutoff = Math.pow(frnd(rng, 1.0f), 5.0f);
		highpassFilterCutoffSlide = Math.pow(frnd(rng, 2.0f) - 1.0f, 5.0f);
		phaserOffset = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f);
		phaserSlide = Math.pow(frnd(rng, 2.0f) - 1.0f, 3.0f);
		repeatSpeed = frnd(rng, 2.0f) - 1.0f;
		arpeggioSpeed = frnd(rng, 2.0f) - 1.0f;
		arpeggioDepth = frnd(rng, 2.0f) - 1.0f;
	}

	/**
	 * Changes the current preset to a predefined preset.
	 *
	 * @param fx FX chosen
	 */
	public void random(RandomGenerator rng, FX fx) {
		resetParams();
		switch (fx) {
		case PICKUP:
			randomPickUp(rng);
			break;
		case LASER:
			randomLaser(rng);
			break;
		case EXPLOSION:
			randomExplosion(rng);
			break;
		case POWERUP:
			randomPowerUp(rng);
			break;
		case HURT:
			randomHurt(rng);
			break;
		case JUMP:
			randomJump(rng);
			break;
		case BEEP:
			randomBeep(rng);
			break;
		default:
			break;
		}
	}

	private void randomBeep(RandomGenerator rng) {
		waveType = rndToWaveForm(rng, 1);
		if (waveType == WaveForm.NOISE) {
			squareDuty = frnd(rng, 0.6f);
		}

		startFrequency = 0.2f + frnd(rng, 0.4f);
		attackTime = 0.0f;
		sustainTime = 0.1f + frnd(rng, 0.1f);
		decayTime = frnd(rng, 0.2f);
		highpassFilterCutoff = 0.1f;
	}

	private void randomExplosion(RandomGenerator rng) {
		waveType = WaveForm.NOISE;
		if (rnd(rng, 1) > 0) {
			startFrequency = 0.1f + frnd(rng, 0.4f);
			pitchSlide = -0.1f + frnd(rng, 0.4f);
		} else {
			startFrequency = 0.2f + frnd(rng, 0.7f);
			pitchSlide = -0.2f - frnd(rng, 0.2f);
		}
		startFrequency *= startFrequency;
		if (rnd(rng, 4) == 0) {
			pitchSlide = 0.0f;
		}
		if (rnd(rng, 2) == 0) {
			repeatSpeed = 0.3f + frnd(rng, 0.5f);
		}
		attackTime = 0.0f;
		sustainTime = 0.1f + frnd(rng, 0.3f);
		decayTime = frnd(rng, 0.5f);
		if (rnd(rng, 1) == 0) {
			phaserOffset = -0.3f + frnd(rng, 0.9f);
			phaserSlide = -frnd(rng, 0.3f);
		}
		sustainPunch = 0.2f + frnd(rng, 0.6f);
		if (rnd(rng, 1) > 0) {
			vibratoStrength = frnd(rng, 0.7f);
			vibratoSpeed = frnd(rng, 0.6f);
		}
		if (rnd(rng, 2) == 0) {
			arpeggioSpeed = 0.6f + frnd(rng, 0.3f);
			arpeggioDepth = 0.8f - frnd(rng, 1.6f);
		}
	}

	private void randomHurt(RandomGenerator rng) {
		waveType = rndToWaveForm(rng, 2);
		if (waveType == WaveForm.SINE) {
			waveType = WaveForm.NOISE;
		}
		if (waveType == WaveForm.SQUARE) {
			squareDuty = frnd(rng, 0.6f);
		}
		startFrequency = 0.2f + frnd(rng, 0.6f);
		pitchSlide = -0.3f - frnd(rng, 0.4f);
		attackTime = 0.0f;
		sustainTime = frnd(rng, 0.1f);
		decayTime = 0.1f + frnd(rng, 0.2f);
		if (rnd(rng, 1) > 0) {
			highpassFilterCutoff = frnd(rng, 0.3f);
		}
	}

	private void randomJump(RandomGenerator rng) {
		waveType = WaveForm.SQUARE;
		squareDuty = frnd(rng, 0.6f);
		startFrequency = 0.3f + frnd(rng, 0.3f);
		pitchSlide = 0.1f + frnd(rng, 0.2f);
		attackTime = 0.0f;
		sustainTime = 0.1f + frnd(rng, 0.3f);
		decayTime = 0.1f + frnd(rng, 0.2f);
		if (rnd(rng, 1) > 0) {
			highpassFilterCutoff = frnd(rng, 0.3f);
		}
		if (rnd(rng, 1) > 0) {
			lowpassFilterCutoff = 1.0f - frnd(rng, 0.6f);
		}
	}

	private void randomLaser(RandomGenerator rng) {
		waveType = rndToWaveForm(rng, 2);
		if (waveType == WaveForm.SINE && rnd(rng, 1) > 0) {
			waveType = rndToWaveForm(rng, 1);
		}
		startFrequency = 0.5f + frnd(rng, 0.5f);
		minimalFrequency = startFrequency - 0.2f - frnd(rng, 0.6f);
		if (minimalFrequency < 0.2f) {
			minimalFrequency = 0.2f;
		}
		pitchSlide = -0.15f - frnd(rng, 0.2f);
		if (rnd(rng, 2) == 0) {
			startFrequency = 0.3f + frnd(rng, 0.6f);
			minimalFrequency = frnd(rng, 0.1f);
			pitchSlide = -0.35f - frnd(rng, 0.3f);
		}
		if (rnd(rng, 1) > 0) {
			squareDuty = frnd(rng, 0.5f);
			squareDutySlide = frnd(rng, 0.2f);
		} else {
			squareDuty = 0.4f + frnd(rng, 0.5f);
			squareDutySlide = -frnd(rng, 0.7f);
		}
		attackTime = 0.0f;
		sustainTime = 0.1f + frnd(rng, 0.2f);
		decayTime = frnd(rng, 0.4f);
		if (rnd(rng, 1) > 0) {
			sustainPunch = frnd(rng, 0.3f);
		}
		if (rnd(rng, 2) == 0) {
			phaserOffset = frnd(rng, 0.2f);
			phaserSlide = -frnd(rng, 0.2f);
		}
		if (rnd(rng, 1) > 0) {
			highpassFilterCutoff = frnd(rng, 0.3f);
		}
	}

	private void randomPickUp(RandomGenerator rng) {
		startFrequency = 0.4f + frnd(rng, 0.5f);
		attackTime = 0.0f;
		sustainTime = frnd(rng, 0.1f);
		decayTime = 0.1f + frnd(rng, 0.4f);
		sustainPunch = 0.3f + frnd(rng, 0.3f);
		if (rnd(rng, 1) > 0) {
			arpeggioSpeed = 0.5f + frnd(rng, 0.2f);
			arpeggioDepth = 0.2f + frnd(rng, 0.4f);
		}
	}

	private void randomPowerUp(RandomGenerator rng) {
		if (rnd(rng, 1) > 0) {
			waveType = WaveForm.SAWTOOTH;
		} else {
			squareDuty = frnd(rng, 0.6f);
		}
		if (rnd(rng, 1) > 0) {
			startFrequency = 0.2f + frnd(rng, 0.3f);
			pitchSlide = 0.1f + frnd(rng, 0.4f);
			repeatSpeed = 0.4f + frnd(rng, 0.4f);
		} else {
			startFrequency = 0.2f + frnd(rng, 0.3f);
			pitchSlide = 0.05f + frnd(rng, 0.2f);
			if (rnd(rng, 1) > 0) {
				vibratoStrength = frnd(rng, 0.7f);
				vibratoSpeed = frnd(rng, 0.6f);
			}
		}
		attackTime = 0.0f;
		sustainTime = frnd(rng, 0.4f);
		decayTime = 0.1f + frnd(rng, 0.4f);
	}

	/**
	 * Clears the parameters to a default value
	 */
	private void resetParams() {
		waveType = WaveForm.SQUARE;

		startFrequency = 0.3f;
		minimalFrequency = 0.0f;
		pitchSlide = 0.0f;
		pitchDeltaSlide = 0.0f;
		squareDuty = 0.0f;
		squareDutySlide = 0.0f;

		vibratoStrength = 0.0f;
		vibratoSpeed = 0.0f;

		attackTime = 0.0f;
		sustainTime = 0.3f;
		decayTime = 0.4f;
		sustainPunch = 0.0f;

		lowpassFilterResonance = 0.0f;
		lowpassFilterCutoff = 1.0f;
		lowpassFilterCutoffSlide = 0.0f;
		highpassFilterCutoff = 0.0f;
		highpassFilterCutoffSlide = 0.0f;

		phaserOffset = 0.0f;
		phaserSlide = 0.0f;

		repeatSpeed = 0.0f;

		arpeggioSpeed = 0.0f;
		arpeggioDepth = 0.0f;
	}

	/**
	 * @param arpeggioDepth the arpeggioDepth to set
	 */
	public void setArpeggioDepth(double arpeggioDepth) {
		this.arpeggioDepth = arpeggioDepth;
	}

	/**
	 * @param arpeggioSpeed the arpeggioSpeed to set
	 */
	public void setArpeggioSpeed(double arpeggioSpeed) {
		this.arpeggioSpeed = arpeggioSpeed;
	}

	/**
	 * @param attackTime the attackTime to set
	 */
	public void setAttackTime(double attackTime) {
		this.attackTime = attackTime;
	}

	/**
	 * @param decayTime the decayTime to set
	 */
	public void setDecayTime(double decayTime) {
		this.decayTime = decayTime;
	}

	/**
	 * @param highpassFilterCutoff the highpassFilterCutoff to set
	 */
	public void setHighpassFilterCutoff(double highpassFilterCutoff) {
		this.highpassFilterCutoff = highpassFilterCutoff;
	}

	/**
	 * @param highpassFilterCutoffSlide the highpassFilterCutoffSlide to set
	 */
	public void setHighpassFilterCutoffSlide(double highpassFilterCutoffSlide) {
		this.highpassFilterCutoffSlide = highpassFilterCutoffSlide;
	}

	/**
	 * @param lowpassFilterCutoff the lowpassFilterCutoff to set
	 */
	public void setLowpassFilterCutoff(double lowpassFilterCutoff) {
		this.lowpassFilterCutoff = lowpassFilterCutoff;
	}

	/**
	 * @param lowpassFilterCutoffSlide the lowpassFilterCutoffSlide to set
	 */
	public void setLowpassFilterCutoffSlide(double lowpassFilterCutoffSlide) {
		this.lowpassFilterCutoffSlide = lowpassFilterCutoffSlide;
	}

	/**
	 * @param lowpassFilterResonance the lowpassFilterResonance to set
	 */
	public void setLowpassFilterResonance(double lowpassFilterResonance) {
		this.lowpassFilterResonance = lowpassFilterResonance;
	}

	/**
	 * @param masterVolume the masterVolume to set
	 */
	public void setMasterVolume(double masterVolume) {
		this.masterVolume = masterVolume;
	}

	/**
	 * @param minimalFrequency the minimalFrequency to set
	 */
	public void setMinimalFrequency(double minimalFrequency) {
		this.minimalFrequency = minimalFrequency;
	}

	/**
	 * @param phaserOffset the phaserOffset to set
	 */
	public void setPhaserOffset(double phaserOffset) {
		this.phaserOffset = phaserOffset;
	}

	/**
	 * @param phaserSlide the phaserSlide to set
	 */
	public void setPhaserSlide(double phaserSlide) {
		this.phaserSlide = phaserSlide;
	}

	/**
	 * @param pitchDeltaSlide the pitchDeltaSlide to set
	 */
	public void setPitchDeltaSlide(double pitchDeltaSlide) {
		this.pitchDeltaSlide = pitchDeltaSlide;
	}

	/**
	 * @param pitchSlide the pitchSlide to set
	 */
	public void setPitchSlide(double pitchSlide) {
		this.pitchSlide = pitchSlide;
	}

	/**
	 * @param repeatSpeed the repeatSpeed to set
	 */
	public void setRepeatSpeed(double repeatSpeed) {
		this.repeatSpeed = repeatSpeed;
	}

	/**
	 * @param soundVolume the soundVolume to set
	 */
	public void setSoundVolume(double soundVolume) {
		this.soundVolume = soundVolume;
	}

	/**
	 * @param squareDuty the squareDuty to set
	 */
	public void setSquareDuty(double squareDuty) {
		this.squareDuty = squareDuty;
	}

	/**
	 * @param squareDutySlide the squareDutySlide to set
	 */
	public void setSquareDutySlide(double squareDutySlide) {
		this.squareDutySlide = squareDutySlide;
	}

	/**
	 * @param startFrequency the startFrequency to set
	 */
	public void setStartFrequency(double startFrequency) {
		this.startFrequency = startFrequency;
	}

	/**
	 * @param sustainPunch the sustainPunch to set
	 */
	public void setSustainPunch(double sustainPunch) {
		this.sustainPunch = sustainPunch;
	}

	/**
	 * @param sustainTime the sustainTime to set
	 */
	public void setSustainTime(double sustainTime) {
		this.sustainTime = sustainTime;
	}

	/**
	 * @param vibratoSpeed the vibratoSpeed to set
	 */
	public void setVibratoSpeed(double vibratoSpeed) {
		this.vibratoSpeed = vibratoSpeed;
	}

	/**
	 * @param vibratoStrength the vibratoStrength to set
	 */
	public void setVibratoStrength(double vibratoStrength) {
		this.vibratoStrength = vibratoStrength;
	}

	/**
	 * @param waveType the waveType to set
	 */
	public void setWaveType(WaveForm waveType) {
		this.waveType = waveType;
	}
}
