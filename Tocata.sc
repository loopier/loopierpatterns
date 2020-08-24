// usage
//

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

    *midi { arg key, midiout, channel=0;
        ^super.new(key, \type, \midi, \midiout, midiout, \chan, channel);
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
        Tocata.list;
    }

    *stop {
        currentEnvironment.keysValuesDo {|k,v|
            v.stop;
        }
    }

    *stopAll {
        this.stop;
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
            tocata.rhythm_(this.drumpattern.at(instrument).pseq(inf))
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

    play {
        Ndef(this.proxyname).play;
        this.playing_(1);
    }

    stop {
        Ndef(this.proxyname).stop;
    }

    fadeTime { arg time;
        Ndef(this.proxyname).fadeTime_(time);
    }


    controls {
        this.name.debug("tocata");
        this.instrument.debug("instrument");
        Tocata.controls(this.instrument);
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

