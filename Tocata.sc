//
//	This file is part of LoopierPatterns, a program library for SuperCollider 3
//
//	Created: 2019
//	Copyright (C) 2019 Roger Pibernat
//	Email:
//	URL:
//
//	This program is free software; you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation; either version 2 of the License, or
//	(at your option) any later version.
//
//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.
//
//	You should have received a copy of the GNU General Public License
//	along with this program; if not, write to the Free Software
//	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//
//  This class uses and extends PLbindef from the wonderful miSCellaneous_lib by Daniel Mayer.
//  https://github.com/dkmayer/miSCellaneous_lib

// usage
// to use with ppar:
// Tocata.synth(\acid, \acid);
// Tocata.synth(\bass, \fmbass);
// pdef(\a, ptpar([0.5, ~acid.plbindef, 0, ~bass.plbindef])).play

// usage
// To use with Ppar:
// Tocata.synth(\acid, \acid);
// Tocata.synth(\bass, \fmbass);
// Pdef(\a, Ptpar([0.5, ~acid.plbindef, 0, ~bass.plbindef])).play

Tocata : PLbindef {
    var <>instrumentName;

    *instrument { arg key, instrument;
        // this.init(key);
        var ndefkey = (key ++ "proxy").asSymbol;
        instrument = instrument ? key;
        instrument.debug("instrument");
        key.debug("key");
        ^Ndef(ndefkey, super.new(key, \instrument, instrument)).stop;
    }

    *def { arg ... args;
        ^super.new(*args)
    }

    *synth{ arg key, synth;
        ^this.instrument(key, synth);
    }

    *sample{ arg key, sound, channels=2;
        var ndefkey = (key ++ "proxy").asSymbol;
        // sound = currentEnvironment[sound] ? currentEnvironment[key];
        // this.init(key);
        ^Ndef(ndefkey, super.new(key, \type, \sample, \sound, sound, \channels, channels, \n, 0)).stop;
    }

    *midi { arg key, midiout, channel=0, audioinputchannel=0;
        var ndefkey = (key ++ "proxy").asSymbol;
        Ndef(ndefkey, {
            // var sig, env, amp=1, out=0, pan=0;
            // sig = SoundIn.ar(audioinputchannel!2);
            // sig = sig * amp;
            // Out.ar(out, Pan2.ar(sig, pan));
            SoundIn.ar(audioinputchannel);
        }).play([0,1]).fadeTime_(3);
        ^super.new(key, \type, \midi, \midiout, midiout, \chan, channel, \audioinputchannel, audioinputchannel);
    }

    *cc { arg key, midiout, channel=0;
        ^super.new(key, \type, \midi, \midiout, midiout, \chan, channel, \midicmd, \control, \ctrNum, 0, \control, 64);
    }

    // use tocata as superdirt
    *dirt { arg key, sound;
        SuperDirt.default = ~dirt;
        ^super.new(key, \type, \dirt, \s, sound, \n, 0);
    }

    *tidal { arg key, sound;
        this.dirt(key,sound);
    }

    // provides a harmonic progression with independent timing
    *harmony { arg degrees = [0,4], durs = [4], quant = Quant(4);
        if (Pdef(\harmony).isPlaying) {"Harmony pattern already playing"}{ Pdef(\harmony).play(quant: quant) };
        if (degrees.isArray) { degrees = Pseq(degrees, inf); };
        if (durs.isArray) { durs = Pseq(durs, inf); };
        Pdef(\harmony,
            Pbind(
                \amp, 0,
                \degree, degrees,
                \dur, durs,
            ).collect({|event| ~lastHarmonyEvent = event;})
        );
        //  to play a pbind with harmony add '+ ~harmony' to \degree.
        ~harmony = Pfunc { ~lastHarmonyEvent[\degree] };
    }

    // list all available synths
    *synths { arg loadStoredSynths = true;
        var names = SortedList.new;
        if(loadStoredSynths) {
            SynthDescLib.read;
        };
        SynthDescLib.global.synthDescs.do { |desc|
            if(desc.def.notNil) {
                // Skip names that start with "system_"
                if ("^[^system_|^pbindFx_]".matchRegexp(desc.name)
            ) {
                names.add(desc.name);
            };
        };
    };

    names.collect(_.postln);
    ^names;
        }

        *loadSamples { arg paths = [], s = Server.default;
        var d = Dictionary.new;
        paths.do { |path|
            var name  = PathName(path).folderName;
            d.add(name -> Loopier.loadSamplesArray(path, s));
        };
        // Loopier.listSamples(d);
        ^d;
    }

    *controls { arg synth;
        var controls = List();
        "% controls: ".format(synth).postln;
        SynthDescLib.global.at(synth).controls.do{ |ctl|
            controls.add([ctl.name, ctl.defaultValue]);
        };
        controls.collect(_.postln);
    }

    // lists available instruments (variables)
    *list {
        // currentEnvironment.keys.asArray.sort.collect(_.postln);
        currentEnvironment.keys.asArray.sort.do{|k|
            "% (%)".format(k, currentEnvironment[k].size).postln;
        }
    }

    *samples {
        Tocata.list;
    }

    *instruments {
		// Tocata.list;
		^Tocata.allInstruments;
	}

	*allInstruments {
		var sourceEnvirs = List();
        super.all.do{|x|
			sourceEnvirs.add(x.sourceEnvir);
		};
		^sourceEnvirs;
    }

    // Play the given instruments.
    // \param instruments     PLbindef | Array       Each instrument can be either a PLbindef or
    //                                               an array of [offset, PLbindef], where offset
    //                                               is the number of beats before the instrument
    //                                               starts to play (may be float for off-beat values)
    // Example: Tocata.play(~acid, [0.5, ~bass]) will play ~acid on the first beat and ~bass on beat 1.5
    // Warning!:  For an instrument to keep it's offset while modified outside this function, any
    //            dynamically created variable that is modified must be declared **BEFORE**
    //            Tocata.play(...) is evalueated.  Otherwise, the offset of the Ptpar will be overwitten.
    *play { arg ...instruments;
        var ptpar = [];
        instruments.do { |instr, i|
            // [i, instr].debug("instrument");
            if (instr.offset.isNil) {instr.offset_(0)};
            ptpar = ptpar ++ instr.offset ++ instr.plbindef;
        };

        ptpar.postln;
        Pdef(\band, Ptpar(ptpar)).play;
    }

    *stop {
        Pdef(\band).stop;
    }

    *stopAll {
        this.stop;
    }

    *band {
        ^Pdef(\band);
    }

    controls {
        var controls = List();
        "% controls: ".format(instrumentName).postln;
        SynthDescLib.global.at(this.instrumentName).controls.do{ |ctl|
            controls.add([ctl.name, ctl.defaultValue]);
        };
        controls.collect(_.postln);
    }

}

// A class that assigns a Tocata to each track of a Drum Machine
// usage
// ~bataca = Bataca(kick: Tocata.sample(\kick, ~avlkick), ...);
// ~bataca.pattern(DrumPattern.tinyhouse);
Bataca {
    var <>sounds;
    var <>drumpattern;

    // *new { arg kick, sn, ch, oh, rim, cym, bell, cl, sh, ht, mt, lt;
    //     var sounds = (
    //         \kick: kick,
    //         \sn: sn,
    //         \ch: ch,
    //         \oh: oh,
    //         \rim: rim,
    //         \cym: cym,
    //         \bell: bell,
    //         \cl: cl,
    //         \sh: sh,
    //         \ht: ht,
    //         \mt: mt,
    //         \lt: lt,
    //     );
    //     ^super.newCopyArgs(sounds);
    // }

    //
    *new { arg
        kick=nil, kickchannels=1,
        sn=nil, snchannels=1,
        ch=nil, chchannels=1,
        oh=nil, ohchannels=1,
        rim=nil, rimchannels=1,
        cym=nil, cymchannels=1,
        bell=nil, bellchannels=1,
        cl=nil, clchannels=1,
        sh=nil, shchannels=1,
        ht=nil, htchannels=1,
        mt=nil, mtchannels=1,
        lt=nil, ltchannels=1;
        var sounds = (
            \kick: Tocata.sample(\kick, kick, kickchannels),
            \sn: Tocata.sample(\sn, sn, snchannels),
            \ch: Tocata.sample(\ch, ch, chchannels),
            \oh: Tocata.sample(\oh, oh, ohchannels),
            \rim: Tocata.sample(\rim, rim, rimchannels),
            \cym: Tocata.sample(\cym, cym, cymchannels),
            \bell: Tocata.sample(\bell, bell, bellchannels),
            \cl: Tocata.sample(\cl, cl, clchannels),
            \sh: Tocata.sample(\sh, sh, shchannels),
            \ht: Tocata.sample(\ht, ht, htchannels),
            \mt: Tocata.sample(\mt, mt, mtchannels),
            \lt: Tocata.sample(\lt, lt, ltchannels),
        );
        ^super.newCopyArgs(sounds);
    }


	play { arg quant = 4;
        // plays the Ndef
        sounds.do{|it|
			var tocata = currentEnvironment[it.source.key];
			tocata.dur_(1/4);
			// tocata.legato_(4);
			it.play.quant_(quant);
		};
    }

	stop {
        // stops the Tocata inside the Ndef (see Tocata.stop
        // -- actually PLbinefEnvironment.stop overriding)
        // It's done like this because if we stop the Ndef all
        // effects will be stopped immediately and we want them
        // to keep going until they are done (e.g. reverb)
        sounds.do{|it|
            it.stop;
        };
    }

    pattern_ { arg pattern;
        this.drumpattern = pattern;
		sounds.do{|it|
			var instrument = it.source.key;
			var tocata = currentEnvironment[instrument];
            // it.key.debug("it");
            // instrument.debug("instrument");
            // tocata.key.debug("tocata");
            pattern.debug("pattern:");
            // tocata.rhythm_(this.drumpattern.at(instrument).pseq(inf))
            tocata.r_(this.drumpattern.at(instrument).pseq(inf))
		}
        ^this.drumpattern;
    }

    pattern {
        ^this.drumpattern.pattern;
    }

    all {
        var players = List.new;
        sounds.do{|sound| players.add(currentEnvironment[sound.source.key])};
        ^players;
    }
}

+ PLbindefEnvironment {
    proxyname {
        ^(this.name ++ "proxy").asSymbol;
    }

    ndef {
        ^Ndef(this.proxyname);
    }

    play {
        Ndef(this.proxyname).play;
        this.plbindef.play;
        // this.playing_(1);
		// var type = this.plbindef.source.at(\type).source;
		// if ( type == \midi) {
		// 	this.plbindef.play;
		// 	// TODO: !!! Capture audio input to enable filtering and fx
		// 	//           Maybe with Ndef(this.proxyname, {SoundIn.ar(inputchannel)}) where
		// 	//           'inputchannel' is an arg to this function
		// };
		// Ndef((this.proxyname++"audio").asSymbol, {SoundIn.ar(this.audioinputchannel.debug("audioinput"))}).play;
    }

    stop {
		// var type = this.plbindef.source.at(\type).source;
		// if ( type == \midi) {
		// 	this.plbindef.stop;
		// };
        // Ndef(this.proxyname).stop;
        this.plbindef.stop;
        // this.playing_(0);
    }

    fadeTime { arg time;
        Ndef(this.proxyname).fadeTime_(time);
    }


    controls {
        this.name.debug("tocata");
        this.instrument.debug("instrument");
        Tocata.controls(this.instrument);
    }

	// Set motifs quickly with degree/dur pairs
	motif { arg motif;
		motif.debug("motif");
		if (motif.notes.isNil)  {this.note_(nil)}  { this.note_(Pseq(motif.notes, inf))};
		if (motif.degrees.isNil){this.degree_(nil)}{ this.degree_(Pseq(motif.degrees, inf))};
		if (motif.durs.isNil)   {this.dur_(nil)}   { this.dur_(Pseq(motif.durs, inf))};
	}

    fade { arg steps = 10, from = 0.0, to = 0.3;
        var list = Array.interpolation(steps, from, to);
		var last = list[list.size - 1];
        list.debug("fade");
		this.amp_(Pseq([Pseq(list), Pseq([last], inf)]));
    }

    fadein { arg steps = 10, to = 0.3;
        this.fade(steps, 0.0, to);
    }

    fadeout { arg steps = 10, from = 0.3;
        this.fade(steps, from, 0.0);
    }

    // Creates an effect with patternable parameters
    // This is an abstraction that allows for specific fx and filter codes below to
    // be shorter and cleaner.
    // \param    index    Number    Index of the slot in the Ndef \param    function Function  Filter function
    // \param    args     Array     Pbind pairs.  If first argument is <= the effect is cancelled
    //
    // Can have an independent duration passed in 'args' with [dur: Pseq([1,2],inf)]
    addFx { arg index, function, args;
        var proxy = Ndef(this.proxyname);
        var first = args[1];
        index.debug("index");
        function.debug("func");
        args.debug("args");
        first.debug("first");
        // one-liner breaks the code: if (first.isNumber && first <= 0)
        if (first.isNumber) {
            first.isNumber.debug("first is number");
            if (first <= 0) {
                proxy[index] = nil;
                proxy[index * 10] = nil;
            } {
                proxy[index] = \pset -> Pbind(*args);
                proxy[index * 10] = \filter -> function;
            };
        } {
            if (first.isFunction) {
                proxy[index] = \pset -> first;
            } {
                proxy[index] = \pset -> Pbind(*args);
            };
            proxy[index * 10] = \filter -> function;
        };
    }

    delay { arg time = 0.2, feedback = 0.5, dur = 1;
        var filterfunc = {|in|
                     var maxdelaytime = \maxdelaytime.kr(8);
                     var delaytime = \time.kr(0.2);
                     var fb = \feedback.kr(0.5);
                     var local, del;
                     local = LocalIn.ar(2) + in;
                     del = DelayN.ar(
                         local,
                         maxdelaytime: maxdelaytime,
                         delaytime: delaytime,
                         mul: fb
                     );
                     LocalOut.ar(del);
                     // SelectX.ar(time, [in, in + del]);
                     in + del;
                 };

        this.addFx(index: 1, function: filterfunc, args: [time:time, feedback: feedback, dur: dur]);
    }

    gverb { arg room = 0.3, size = 0.03, dur = 1;
        var filterfunc = {|in|
            var roomsize = \size.kr(0.03).linlin(0.0, 1.0, 1, 300);
            var mul = \room.kr(0.3);
            in + GVerb.ar(in, roomsize: roomsize, mul: mul);
        };
        this.addFx(index: 2, function: filterfunc, args: [room:room, size: size, dur: dur]);
    }

    freeverb { arg mix = 0.33, room = 0.5, dur = 1;
        var filterfunc = {|in|
            var roomsize = \room.kr(0.5);
            var mix = \mix.kr(0.33);
            FreeVerb.ar(in, mix: mix, room: roomsize);
        };
        this.addFx(index: 3, function: filterfunc, args: [mix:mix, room: room, dur: dur]);
    }

    lpf { arg cutoff = 440, rq = 0.2, dur = 1;
                var filterfunc =  {|in|
                    var freq = \cutoff.kr(440);
                    var resonance = \rq.kr(0.2);
                    RLPF.ar(in, freq: freq, rq: resonance);
                };
        this.addFx(index: 4, function: filterfunc, args: [cutoff:cutoff, rq: rq, dur: dur]);
    }

    hpf { arg cutoff = 440, rq = 0.2, dur = 1;
        var filterfunc =  {|in|
            var freq = \cutoff.kr(440);
            var resonance = \rq.kr(0.2);
            RHPF.ar(in, freq: freq, rq: resonance);
        };
        this.addFx(index: 4, function: filterfunc, args: [cutoff:cutoff, rq: rq, dur: dur]);
    }

    bpf { arg cutoff = 440, rq = 0.2, dur = 1;
        var filterfunc =  {|in|
            var freq = \cutoff.kr(440);
            var resonance = \rq.kr(0.2);
            RHPF.ar(in, freq: freq, rq: resonance);
        };
        this.addFx(index: 4, function: filterfunc, args: [cutoff:cutoff, rq: rq, dur: dur]);
    }

    distort { arg distort = 0.3, dur = 1;
                var filterfunc = { |in|
                    var dist = \distortion.kr(0.3);
                    var signal, mod;
                    signal = in;
                    mod = CrossoverDistortion.ar(signal, amp: 0.2, smooth: 0.01);
                    mod = mod + (0.1 * dist * DynKlank.ar(`[[60,61,240,3000 + SinOsc.ar(62,mul: 100)],nil,[0.1, 0.1, 0.05, 0.01]], signal));
                    mod = (mod.cubed * 8).softclip * 0.5;
                    mod = SelectX.ar(dist, [signal, mod]);
                };
        this.addFx(index: 4, function: filterfunc, args: [distort: distort, dur: dur]);
    }

    // add filters like you would do with Ndef:
    // Ndef(\a)[x] = \filter -> { ... }
    fx1 { arg func;
        var proxy = Ndef(this.proxyname);
        if (func.isNil) {
            proxy[100] = nil
        }{
            proxy[100] = \filter -> func;
        }
    }

    fx2 { arg func;
        var proxy = Ndef(this.proxyname);
        if (func.isNil) {
            proxy[200] = nil
        }{
            proxy[200] = \filter -> func;
        }
    }

    fx3 { arg func;
        var proxy = Ndef(this.proxyname);
        if (func.isNil) {
            proxy[300] = nil
        }{
            proxy[300] = \filter -> func;
        }
    }
}