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

	*instrument { arg key, instrument;
		instrument = instrument ? key;
		instrument.debug("instrument");
		key.debug("key");
		^super.new(key, \instrument, instrument);
	}

	*def { arg ... args;
		^super.new(*args)
	}

	*synth{ arg key, synth;
		this.(key, synth);
	}

	*sample{ arg key, sound, channels=2;
		// sound = currentEnvironment[sound] ? currentEnvironment[key];
		^super.new(key, \type, \sample, \sound, sound, \channels, channels, \n, 0);
	}

	*midi { arg key, midiout, channel=0;
		^super.new(key, \type, \midi, \midiout, midiout, \chan, channel);
	}

	// use tocata as superdirt
	*dirt { arg key, sound;
		SuperDirt.default = ~dirt;
		^super.new(key, \type, \dirt, \s, sound, \n, 0);
	}

	*tidal { arg key, sound;
		this.dirt(key,sound);
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
		currentEnvironment.keys.asArray.sort.collect(_.postln);
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

