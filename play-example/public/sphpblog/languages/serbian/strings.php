<?php

	// Serbian Language File
	// (c) 2006. Milan Milanovic, milan <at> milanovic <dot> org
	//

	// Simple PHP Version: 0.4.1
	// Language Version:   0.4.1.0

	function sb_language( $page ="") {
		global $lang_string;

		// Language: Serbian latin
		$lang_string['language'] = 'serbian';

		$lang_string['locale'] = array('sr', 'srp'); 'SRP';
		$lang_string['rss_locale'] = 'sr'; // New 0.4.8

		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'UTF-8';
		$lang_string['php_charset'] = 'UTF-8';

		setlocale( LC_TIME, $lang_string['locale'] );

	}
?>
