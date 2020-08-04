// usage
//

Tocata : PLbindef {
	var <>instrumentName;

	// This will load stored synthdefs and Superdirt samples (if installed).
	// *new { arg path, loadStoredSynths = true;
	// 	path = path ? Platform.userAppSupportDir ++ "/downloaded-quarks/Dirt-Samples";
	// 	// initialize fx
	// 	// Pdef(\reverb, Pmono(\reverb, ...))
	// 	Ndef(\reverb, {GVerb.ar(In.ar())})
	// }

	// *init { arg key;
	// 	var busname =(key++"bus").asSymbol;
	// 	var fxname =(key++"fx").asSymbol;
	// 	var bus = Bus.audio(Server.default, 2);
	// 	currentEnvironment.add(busname -> bus);
	// 	Ndef(fxname, {In.ar(currentEnvironment.at(busname),2)}).play;
	// 	[fxname].debug("init");
	// 	[bus].debug("bus");
	// 	[currentEnvironment.at(busname)].debug("busname");
	// }

	*instrument { arg key, instrument;
		// this.init(key);
        var ndefkey = (key ++ "proxy").asSymbol;
		instrument = instrument ? key;
		instrument.debug("instrument");
		key.debug("key");
		^Ndef(ndefkey, super.new(key, \instrument, instrument));
	}

	*def { arg ... args;
		^super.new(*args)
	}

	*synth{ arg key, synth;
		this.instrument(key, synth);
	}

	*sample{ arg key, sound, channels=2;
        var ndefkey = (key ++ "proxy").asSymbol;
		// sound = currentEnvironment[sound] ? currentEnvironment[key];
		// this.init(key);
		^Ndef(ndefkey, super.new(key, \type, \sample, \sound, sound, \channels, channels, \n, 0));
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
		Pdef(\harmony,
			Pbind(
				\amp, 0,
				\degree, Pseq(degrees, inf),
				\dur, Pseq(durs, inf)
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
    *instruments {
        // currentEnvironment.keys.asArray.sort.collect(_.postln);
        currentEnvironment.keys.asArray.sort.do{|k|
            "% (%)".format(k, currentEnvironment[k].size).postln;
        }
    }

    *samples {
        Tocata.instruments;
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


+ PLbindefEnvironment {
    proxyname {
        ^(this.name ++ "proxy").asSymbol;
    }

	play {
        Ndef(this.proxyname).play;
    }

    stop {
        // Ndef(this.proxyname).stop;
        // play a rest and stop
        this.rhythm_([\r].pseq(1));
    }

    fadeTime { arg time;
        Ndef(this.proxyname).fadeTime_(time);
    }


	controls {
		this.name.debug("tocata");
		this.instrument.debug("instrument");
		Tocata.controls(this.instrument);
	}

	delay { arg time = 0.2, feedback = 0.5;
        var proxy = Ndef(this.proxyname);
        if (time.isNumber) { 
            if (time <= 0) {
                proxy[1] = nil;
                proxy[10] = nil;
            }
        } {
            proxy[1] = \pset -> Pbind(\time, time, \feedback, feedback);
            proxy[10] = \filter -> {|in|
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
                SelectX.ar(time, [in, in + del]);
            };
        };
	}

	gverb { arg room = 0.3, size = 0.03;
        var proxy = Ndef(this.proxyname);
        size = size.linlin(0.0, 1.0, 1, 300);
        if (room.isNumber) { 
        if (room <= 0) {
            proxy[2] = nil;
            proxy[20] = nil;
            
            }
        } {
            proxy[2] = \pset -> Pbind(\mul, room, \size, size);
            proxy[20] = \filter -> {|in|
                var roomsize = \size.kr(0.03);
                var mul = \room.kr(0.3);
                in + GVerb.ar(in, roomsize: roomsize, mul: mul);
            };
        };
	}

	freeverb { arg mix = 0.33, room = 0.5;
        var proxy = Ndef(this.proxyname);
        if (mix.isNumber) { 
        if (mix <= 0) {
            proxy[2] = nil;
            proxy[20] = nil;
            
            }
        } {
            proxy[2] = \pset -> Pbind(\mix, mix, \room, room);
            proxy[20] = \filter -> {|in|
                var roomsize = \room.kr(0.5);
                var mix = \mix.kr(0.33);
                FreeVerb.ar(in, mix: mix, room: roomsize);
            };
        };
	}

	lpf { arg cutoff = 440, rq = 0.2;
        var proxy = Ndef(this.proxyname);
		if (cutoff.isNumber) {
			// cannot be in the same statement because if 'cutoff' is 
            // a pattern it breaks
			if (cutoff <= 0){
				proxy[3] = nil;
				proxy[30] = nil;
			}
        } {
            proxy[3] = \pset -> Pbind(\cutoff, cutoff, \rq, rq);
            proxy[30] = \filter -> {|in|
                var freq = \cutoff.kr(440);
                var resonance = \rq.kr(0.2);
                RLPF.ar(in, freq: freq, rq: resonance);
            };
        };
	}

	hpf { arg cutoff = 440, rq = 0.2;
        var proxy = Ndef(this.proxyname);
		if (cutoff.isNumber) {
			// cannot be in the same statement because if 'cutoff' is 
            // a pattern it breaks
			if (cutoff <= 0){
				proxy[4] = nil;
				proxy[40] = nil;
			}
        } {
            proxy[4] = \pset -> Pbind(\cutoff, cutoff, \rq, rq);
            proxy[40] = \filter -> {|in|
                var freq = \cutoff.kr(440);
                var resonance = \rq.kr(0.2);
                RHPF.ar(in, freq: freq, rq: resonance);
            };
        };
	}

	bpf { arg cutoff = 440, rq = 0.2;
        var proxy = Ndef(this.proxyname);
		if (cutoff.isNumber) {
			// cannot be in the same statement because if 'cutoff' is 
            // a pattern it breaks
			if (cutoff <= 0){
				proxy[5] = nil;
				proxy[50] = nil;
			}
        } {
            proxy[5] = \pset -> Pbind(\cutoff, cutoff, \rq, rq);
            proxy[50] = \filter -> {|in|
                var freq = \cutoff.kr(440);
                var resonance = \rq.kr(0.2);
                BPF.ar(in, freq: freq, rq: resonance);
            };
        };
	}

	distort { arg distort = 0.3;
        var proxy = Ndef(this.proxyname);
        if (distort.isNumber){ 
			// cannot be in the same statement because if 'distort' is 
            // a pattern it breaks
            if (distort <= 0) {
                proxy[6] = nil;
                proxy[60] = nil;
            }
        } {
            proxy[6] = \pset -> Pbind(\distortion, distort);
            proxy[60] = \filter -> { |in|
                var dist = \distortion.kr(0.3);
                var signal, mod;
                signal = in;
                mod = CrossoverDistortion.ar(signal, amp: 0.2, smooth: 0.01);
                mod = mod + (0.1 * dist * DynKlank.ar(`[[60,61,240,3000 + SinOsc.ar(62,mul: 100)],nil,[0.1, 0.1, 0.05, 0.01]], signal));
                mod = (mod.cubed * 8).softclip * 0.5;
                mod = SelectX.ar(dist, [signal, mod]);
            };
        };
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

    fx1 { arg func;
        var proxy = Ndef(this.proxyname);
        if (func.isNil) { 
            proxy[300] = nil 
        }{ 
            proxy[300] = \filter -> func;
        }    
    }
}
