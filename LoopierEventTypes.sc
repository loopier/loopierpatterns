// Example use:
// (
// Pdef(\x, Pbind(
// 	\type, \sample,
// 	\channels, 1,
// 	\sound, ~avlkick,
// 	\n, Pwhite(0,9),
// )).play
// )

LoopierEventTypes {
	*new {
		Event.addEventType(\sample, { |server|
			~sound = ~sound ? [];
			~n = ~n ? 0;
			~channels = ~channels ? 2;
			~instrument = [\playbufm, \playbuf][~channels-1];
			~buf = ~sound.at(~n.mod(~sound.size));
			// TODO: !!! ~note modifies rate
			~type = \note;
			currentEnvironment.play;

			// ~sound.size.debug("num samples");
			// ~channels.debug("channels");
			// ~instrument.debug("ins");
			// ~n.debug("n");
			// ~buf.debug("buf");
			// ~octave.debug;
			// "".postln;
		},
		// defaults
		(legato: 1)
		);

		Event.addEventType(\midiOnCtl, { |server|
			var original = currentEnvironment.copy.put(\type, \midi);
			~midicmd.do { |cmd|
				original.copy.put(\midicmd, cmd).play;
			};
		});


		// Event.addEventType(\speak, { |server|
		// 	~voice = ~voice ? "kal";
		// 	~say = ~say ? "hello";
		// 	~unixcmd = Pfunc{ "mimic -voice % -t %".format(~voice, ~say).unixCmd };
		// 	~amp = 0;
		// 	currentEnvironment.play;
		//
		// 	~unixcmd.debug("unix");
		// 	"".postln;
		// 	},
		// 	// defaults
		// 	(legato: 1)
		// );

	}
}