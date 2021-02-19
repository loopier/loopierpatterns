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
// tocata.synth(\acid, \acid);
// tocata.synth(\bass, \fmbass);
// pdef(\a, ptpar([0.5, ~acid.plbindef, 0, ~bass.plbindef])).play

tocata : plbindef {
    var <>instrumentname;

    *instrument { arg key, instrument;
        // this.init(key);
        var ndefkey = (key ++ "proxy").assymbol;
        var plbindef = super.new(key, \instrument, instrument);
        instrument = instrument ? key;
        instrument.debug("instrument");
        key.debug("key");
        ^ndef(ndefkey, plbindef).stop.fadetime_(3);
        // ndef(ndefkey, plbindef).stop.fadetime_(3);
        // ^plbindef.stop.fadetime_(3);
    }

    *def { arg ... args;
        ^super.new(*args)
    }

    *synth{ arg key, synth;
        ^this.instrument(key, synth);
    }

    *sample{ arg key, sound, channels=2;
        var ndefkey = (key ++ "proxy").assymbol;
        // sound = currentenvironment[sound] ? currentenvironment[key];
        // this.init(key);
        sound[0].class.postln;
        sound[0].numchannels.postln;
        ^ndef(ndefkey, super.new(key, \type, \sample, \sound, sound, \channels, channels, \n, 0)).stop.fadetime_(3);
    }

    *midi { arg key, midiout, channel=0;
        var ndefkey = (key ++ "proxy").assymbol;
        tocata.cc(key, midiout, channel);
        ^super.new(key, \type, \midi, \midiout, midiout, \chan, channel);
    }

    *midiwithaudio { arg key, midiout, channel=0, audioinputchannel=0;
        var ndefkey = (key ++ "proxy").assymbol;
        ndef(ndefkey, {
            // var sig, env, amp=1, out=0, pan=0;
            // sig = soundin.ar(audioinputchannel!2);
            // sig = sig * amp;
            // out.ar(out, pan2.ar(sig, pan));
            soundin.ar(audioinputchannel);
        }).play([0,1]).fadetime_(3).quant_(1);
        tocata.cc(key, midiout, channel);
        ^super.new(key, \type, \midi, \midiout, midiout, \chan, channel, \audioinputchannel, audioinputchannel);
    }


    *cc { arg key, midiout, channel=0;
        ^super.new((key++"cc").assymbol, \type, \midi, \midiout, midiout, \chan, channel, \midicmd, \control);
    }

    // use tocata as superdirt
    *dirt { arg key, sound;
        superdirt.default = ~dirt;
        ^super.new(key, \type, \dirt, \s, sound, \n, 0);
    }

    *tidal { arg key, sound;
        this.dirt(key,sound);
    }

    // provides a harmonic progression with independent timing
    *harmony { arg degrees = [0,4], durs = [4], quant = quant(4);
        if (pdef(\harmony).isplaying) {"harmony pattern already playing"}{ pdef(\harmony).play(quant: quant) };
        if (degrees.isarray) { degrees = pseq(degrees, inf); };
        if (durs.isarray) { durs = pseq(durs, inf); };
        pdef(\harmony,
            pbind(
                \amp, 0,
                \degree, degrees,
                \dur, durs,
            ).collect({|event| ~lastharmonyevent = event;})
        );
        //  to play a pbind with harmony add '+ ~harmony' to \degree.
        ~harmony = pfunc { ~lastharmonyevent[\degree] };
    }

    // list all available synths
    *synths { arg loadstoredsynths = true;
        var names = sortedlist.new;
        if(loadstoredsynths) {
            synthdesclib.read;
        };
        synthdesclib.global.synthdescs.do { |desc|
            if(desc.def.notnil) {
                // skip names that start with "system_"
                if ("^[^system_|^pbindfx_]".matchregexp(desc.name)
            ) {
                names.add(desc.name);
            };
        };
    };

    names.collect(_.postln);
    ^names;
        }

        *loadsamples { arg paths = [], s = server.default;
        var d = dictionary.new;
        paths.do { |path|
            var name  = pathname(path).foldername;
            d.add(name -> loopier.loadsamplesarray(path, s));
        };
        // loopier.listsamples(d);
        ^d;
    }

    *controls { arg synth;
        var controls = list();
        "% controls: ".format(synth).postln;
        synthdesclib.global.at(synth).controls.do{ |ctl|
            controls.add([ctl.name, ctl.defaultvalue]);
        };
        controls.collect(_.postln);
    }

    // lists available instruments (variables)
    *list {
        // currentenvironment.keys.asarray.sort.collect(_.postln);
        currentenvironment.keys.asarray.sort.do{|k|
            "% (%)".format(k, currentenvironment[k].size).postln;
        }
    }

    *samples {
        tocata.list;
    }

    *instruments {
		// tocata.list;
		^tocata.allinstruments;
	}

	*allinstruments {
		var sourceenvirs = list();
        super.all.do{|x|
			sourceenvirs.add(x.sourceenvir);
		};
		^sourceenvirs;
    }

    // play the given instruments.
    // \param instruments     plbindef | array       each instrument can be either a plbindef or
    //                                               an array of [offset, plbindef], where offset
    //                                               is the number of beats before the instrument
    //                                               starts to play (may be float for off-beat values)
    // example: tocata.play(~acid, [0.5, ~bass]) will play ~acid on the first beat and ~bass on beat 1.5
    // warning!:  for an instrument to keep it's offset while modified outside this function, any
    //            dynamically created variable that is modified must be declared **before**
    //            tocata.play(...) is evalueated.  otherwise, the offset of the ptpar will be overwitten.
    *play { arg ...instruments;
        var ptpar = [];
        instruments.do { |instr, i|
            // [i, instr].debug("instrument");
            if (instr.offset.isnil) {instr.offset_(0)};
            ptpar = ptpar ++ instr.offset ++ instr.plbindef;
        };

        ptpar.postln;
        pdef(\band, ptpar(ptpar)).play;
    }

    *stop {
        pdef(\band).stop;
    }

    *stopall {
        this.stop;
    }

    *band {
        ^pdef(\band);
    }

    controls {
        var controls = list();
        "% controls: ".format(instrumentname).postln;
        synthdesclib.global.at(this.instrumentname).controls.do{ |ctl|
            controls.add([ctl.name, ctl.defaultvalue]);
        };
        controls.collect(_.postln);
    }

}

// a class that assigns a tocata to each track of a drum machine
// usage
// ~bataca = bataca(kick: tocata.sample(\kick, ~avlkick), ...);
// ~bataca.pattern(drumpattern.tinyhouse);
bataca {
    var <>sounds;
    var <>drumpattern;

    *new { arg kick, sn, ch, oh, rim, cym, bell, cl, sh, ht, mt, lt;
        var sounds = (
            \kick: kick,
            \sn: sn,
            \ch: ch,
            \oh: oh,
            \rim: rim,
            \cym: cym,
            \bell: bell,
            \cl: cl,
            \sh: sh,
            \ht: ht,
            \mt: mt,
            \lt: lt,
        );
        ^super.newcopyargs(sounds);
    }

    *from { arg kick=nil, sn=nil, ch=nil, oh=nil, rim=nil, cym=nil, bell=nil, cl=nil, sh=nil, ht=nil, mt=nil, lt=nil;
        var sounds = (
            \kick: tocata.sample(\kick, kick),
            \sn: tocata.sample(\sn,sn),
            \ch: tocata.sample(\ch,ch),
            \oh: tocata.sample(\oh,oh),
            \rim: tocata.sample(\rim,rim),
            \cym: tocata.sample(\cym,cym),
            \bell:  tocata.sample(\bell,bell),
            \cl: tocata.sample(\cl,cl),
            \sh: tocata.sample(\sh,sh),
            \ht: tocata.sample(\ht,ht),
            \mt: tocata.sample(\mt,mt),
            \lt: tocata.sample(\lt,lt)
        );
        ^super.newcopyargs(sounds);
    }

	play { arg quant = 4;
        // plays the ndef
        sounds.do{|it|
			var tocata = currentenvironment[it.source.key];
			tocata.dur_(1/4);
			// tocata.legato_(4);
			it.play.quant_(quant);
		};
    }

	stop {
        // stops the tocata inside the ndef (see tocata.stop
        // -- actually plbinefenvironment.stop overriding)
        // it's done like this because if we stop the ndef all
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
			var tocata = currentenvironment[instrument];
            // it.key.debug("it");
            // instrument.debug("instrument");
            // tocata.key.debug("tocata");
            pattern.debug("pattern:");
            tocata.rhythm_(this.drumpattern.at(instrument).pseq(inf))
		}
        ^this.drumpattern;
    }

    pattern {
        ^this.drumpattern.pattern;
    }

    all {
        var players = list.new;
        sounds.do{|sound| players.add(currentenvironment[sound.source.key])};
        ^players;
    }
}

+ plbindefenvironment {
    proxyname {
        ^(this.name ++ "proxy").assymbol;
    }

    ndef {
        ^ndef(this.proxyname);
    }

    play {
        ndef(this.proxyname).play;
        // this.plbindef.play;

        // this.playing_(1);
		// var type = this.plbindef.source.at(\type).source;
		// if ( type == \midi) {
		// 	this.plbindef.play;
		// 	// todo: !!! capture audio input to enable filtering and fx
		// 	//           maybe with ndef(this.proxyname, {soundin.ar(inputchannel)}) where
		// 	//           'inputchannel' is an arg to this function
		// };
		// ndef((this.proxyname++"audio").assymbol, {soundin.ar(this.audioinputchannel.debug("audioinput"))}).play;
    }

    stop {
		// var type = this.plbindef.source.at(\type).source;
		// if ( type == \midi) {
		// 	this.plbindef.stop;
		// };
        ndef(this.proxyname).stop;
        // this.plbindef.stop;
        // this.playing_(0);
    }

    cc { arg num=0, value=60;
        ^plbindef((this.name++"cc").assymbol, \midicmd, \control, \ctrnum, num, \control, value.linlin(0.0,1.0,0,127)).play;
    }

    fadetime { arg time;
        ndef(this.proxyname).fadetime_(time);
    }


    controls {
        this.name.debug("tocata");
        this.instrument.debug("instrument");
        tocata.controls(this.instrument);
    }

	// set motifs quickly with degree/dur pairs
	motif { arg motif;
		motif.debug("motif");
		if (motif.notes.isnil)  {this.note_(nil)}  { this.note_(pseq(motif.notes, inf))};
		if (motif.degrees.isnil){this.degree_(nil)}{ this.degree_(pseq(motif.degrees, inf))};
		if (motif.durs.isnil)   {this.dur_(nil)}   { this.dur_(pseq(motif.durs, inf))};
	}

    fade { arg steps = 10, from = 0.0, to = 0.3;
        var list = array.interpolation(steps, from, to);
		var last = list[list.size - 1];
        list.debug("fade");
		this.amp_(pseq([pseq(list), pseq([last], inf)]));
    }

    fadein { arg steps = 10, to = 0.3;
        this.fade(steps, 0.0, to);
    }

    fadeout { arg steps = 10, from = 0.3;
        this.fade(steps, from, 0.0);
    }

    // creates an effect with patternable parameters
    // this is an abstraction that allows for specific fx and filter codes below to
    // be shorter and cleaner.
    // \param    index    number    index of the slot in the ndef \param    function function  filter function
    // \param    args     array     pbind pairs.  if first argument is <= the effect is cancelled
    //
    // can have an independent duration passed in 'args' with [dur: pseq([1,2],inf)]
    addfx { arg index, function, args;
        var proxy = ndef(this.proxyname);
        var first = args[1];
        index.debug("index");
        function.debug("func");
        args.debug("args");
        first.debug("first");
        // one-liner breaks the code: if (first.isnumber && first <= 0)
        if (first.isnumber) {
            first.isnumber.debug("first is number");
            if (first <= 0) {
                proxy[index] = nil;
                proxy[index * 10] = nil;
            } {
                proxy[index] = \pset -> pbind(*args);
                proxy[index * 10] = \filter -> function;
            };
        } {
            if (first.isfunction) {
                proxy[index] = \pset -> first;
            } {
                proxy[index] = \pset -> pbind(*args);
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
                     local = localin.ar(2) + in;
                     del = delayn.ar(
                         local,
                         maxdelaytime: maxdelaytime,
                         delaytime: delaytime,
                         mul: fb
                     );
                     localout.ar(del);
                     // selectx.ar(time, [in, in + del]);
                     in + del;
                 };

        this.addfx(index: 1, function: filterfunc, args: [time:time, feedback: feedback, dur: dur]);
    }

    gverb { arg room = 0.3, size = 0.03, dur = 1;
        var filterfunc = {|in|
            var roomsize = \size.kr(0.03).linlin(0.0, 1.0, 1, 300);
            var mul = \room.kr(0.3);
            in + gverb.ar(in, roomsize: roomsize, mul: mul);
        };
        this.addfx(index: 2, function: filterfunc, args: [room:room, size: size, dur: dur]);
    }

    freeverb { arg mix = 0.33, room = 0.5, dur = 1;
        var filterfunc = {|in|
            var roomsize = \room.kr(0.5);
            var mix = \mix.kr(0.33);
            freeverb.ar(in, mix: mix, room: roomsize);
        };
        this.addfx(index: 3, function: filterfunc, args: [mix:mix, room: room, dur: dur]);
    }

    lpf { arg cutoff = 440, rq = 0.2, dur = 1;
                var filterfunc =  {|in|
                    var freq = \cutoff.kr(440);
                    var resonance = \rq.kr(0.2);
                    rlpf.ar(in, freq: freq, rq: resonance);
                };
        this.addfx(index: 4, function: filterfunc, args: [cutoff:cutoff, rq: rq, dur: dur]);
    }

    hpf { arg cutoff = 440, rq = 0.2, dur = 1;
        var filterfunc =  {|in|
            var freq = \cutoff.kr(440);
            var resonance = \rq.kr(0.2);
            rhpf.ar(in, freq: freq, rq: resonance);
        };
        this.addfx(index: 4, function: filterfunc, args: [cutoff:cutoff, rq: rq, dur: dur]);
    }

    bpf { arg cutoff = 440, rq = 0.2, dur = 1;
        var filterfunc =  {|in|
            var freq = \cutoff.kr(440);
            var resonance = \rq.kr(0.2);
            rhpf.ar(in, freq: freq, rq: resonance);
        };
        this.addfx(index: 4, function: filterfunc, args: [cutoff:cutoff, rq: rq, dur: dur]);
    }

    distort { arg distort = 0.3, dur = 1;
                var filterfunc = { |in|
                    var dist = \distortion.kr(0.3);
                    var signal, mod;
                    signal = in;
                    mod = crossoverdistortion.ar(signal, amp: 0.2, smooth: 0.01);
                    mod = mod + (0.1 * dist * dynklank.ar(`[[60,61,240,3000 + sinosc.ar(62,mul: 100)],nil,[0.1, 0.1, 0.05, 0.01]], signal));
                    mod = (mod.cubed * 8).softclip * 0.5;
                    mod = selectx.ar(dist, [signal, mod]);
                };
        this.addfx(index: 4, function: filterfunc, args: [distort: distort, dur: dur]);
    }

    // add filters like you would do with ndef:
    // ndef(\a)[x] = \filter -> { ... }
    fx1 { arg func;
        var proxy = ndef(this.proxyname);
        if (func.isnil) {
            proxy[100] = nil
        }{
            proxy[100] = \filter -> func;
        }
    }

    fx2 { arg func;
        var proxy = ndef(this.proxyname);
        if (func.isnil) {
            proxy[200] = nil
        }{
            proxy[200] = \filter -> func;
        }
    }

    fx3 { arg func;
        var proxy = ndef(this.proxyname);
        if (func.isnil) {
            proxy[300] = nil
        }{
            proxy[300] = \filter -> func;
        }
    }
}

