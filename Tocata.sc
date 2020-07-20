// usage
//

Tocata : PLbindef {
	var <>instrumentName;

	// This will load stored synthdefs and Superdirt samples (if installed).
	*new { arg path, loadStoredSynths = true;
		path = path ? Platform.userAppSupportDir ++ "/downloaded-quarks/Dirt-Samples";
		// initialize fx
		// Pdef(\reverb, Pmono(\reverb, ...))
		Ndef(\reverb, {GVerb.ar(In.ar())})
	}

	*init { arg key;
		var busname =(key++"bus").asSymbol;
		var fxname =(key++"fx").asSymbol;
		var bus = Bus.audio(Server.default, 2);
		currentEnvironment.add(busname -> bus);
		Ndef(fxname, {In.ar(currentEnvironment.at(busname),2)}).play;
		[fxname].debug("init");
		[bus].debug("bus");
		[currentEnvironment.at(busname)].debug("busname");
	}

	*instrument { arg key, instrument;
		this.init(key);
		instrument = instrument ? key;
		instrument.debug("instrument");
		key.debug("key");
		^super.new(key, \instrument, instrument);
	}

	*def { arg ... args;
		^super.new(*args)
	}

	*synth{ arg key, synth;
		this.instrument(key, synth);
	}

	*sample{ arg key, sound, channels=2;
		// sound = currentEnvironment[sound] ? currentEnvironment[key];
		this.init(key);
		^super.new(key, \type, \sample, \sound, sound, \channels, channels, \n, 0);
	}

	*midi { arg key, midiout, channel=0;
		^super.new(key, \type, \midi, \midiout, midiout, \chan, channel);
	}

	*cc { arg key, midiout, channel=0;
		^super.new(key, \type, \midi, \midiout, midiout, \chan, channel, \midicmd, \control, \ctrNum, 0, \control, 64);
	}

	*fx { arg key, fx;
		// set 'in' to an bus out of range so it doesn't sound by default
		currentEnvironment.add( key.asSymbol -> Ndef(key.asSymbol, fx).set(\in, 24).play);
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

	// *harmony { arg prog, dur = 1, scale = ;
	// 	this.new(\harmonydef, \amp, 0, \degree, Pseq(prog, inf), \dur, dur);
	// }

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
	controls {
		this.name.debug("tocata");
		this.instrument.debug("instrument");
		Tocata.controls(this.instrument);
	}

	connectfx {
		// connect Pbind out to fx Ndef in.
		var fxname = (this.name ++ "fx").asSymbol;
		var busname = (this.name ++ "bus").asSymbol;
		this.out_(currentEnvironment.at(busname));
		fxname.debug("fxname");
		busname.debug("busname");
		currentEnvironment.at(busname).debug("bus");
		Ndef(fxname).debug("ndef");
		^fxname;
	}

	delay { arg time = 0.2, feedback = 0.5;
		var fxname = this.connectfx;
		// Ndef(fxname)[1] = \filter -> {|in| in + CombC.ar(in, time, time, decay)};
		Ndef(fxname)[1] = \filter -> {|in|
			var maxdelaytime = 8;
			var local, del;
			local = LocalIn.ar(2) + in;
			del = DelayN.ar(
				local,
				maxdelaytime: maxdelaytime,
				delaytime: time,
				mul: feedback
			);
			LocalOut.ar(del);
			SelectX.ar(time, [in, in + del]);
		};
		// if (time <= 0) {Ndef(fxname)[1] = nil};
	}

	gverb { arg amp = 0.3, room = 0.03;
		var fxname = this.connectfx;
		room = room.linlin(0.0, 1.0, 1, 300);
		Ndef(fxname)[2] = \filter -> {|in| GVerb.ar(in, roomsize: room, mul:amp)};
		if (amp <= 0) {Ndef(fxname)[2] = nil};
	}

	freeverb { arg mix = 0.33, room = 0.5;
		var fxname = this.connectfx;
		Ndef(fxname)[2] = \filter -> {|in| FreeVerb.ar(in, mix: mix, room: room)};
		if (mix <= 0) {Ndef(fxname)[2] = nil};
	}

	distort { arg distort = 0;
		var fxname = this.connectfx;
		Ndef(fxname)[3] = \filter -> { |in|
			var signal, mod;
			signal = in;
			mod = CrossoverDistortion.ar(signal, amp: 0.2, smooth: 0.01);
			mod = mod + (0.1 * distort * DynKlank.ar(`[[60,61,240,3000 + SinOsc.ar(62,mul: 100)],nil,[0.1, 0.1, 0.05, 0.01]], signal));
			mod = (mod.cubed * 8).softclip * 0.5;
			mod = SelectX.ar(distort, [signal, mod]);

			// var abs, excess, output;
			// abs = in.abs;
			// excess = (abs-0.1).max(0.0).min(0.9)/0.9;
			// //original plus sinusoidal perturbation of amount based on absolute amplitude
			// in + (excess*(sin(excess*2pi*5)*0.5-0.5));
		};
		// if (distort <= 0) {Ndef(fxname)[3] = nil};
	}
}