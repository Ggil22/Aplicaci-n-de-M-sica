package com.padsof.muflix.testers;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AlbumTester.class,
				AnonimoTester.class,
				CancionTester.class,
				DenunciaTester.class,
				PlaylistTester.class
				//CancionTester.class
				// PujaTest.class,
				// VIPTest.class,
			})

public class AllTesters {

}
