<?php

	// Slovenian Language File
	// (c) 2005 sverde1, sverde1 <at> email <dot> si
	//

	// Simple PHP Version: 0.4.6.2
	// Language Version:   0.1


	function sb_language( $page ="") {
		global $lang_string;

		// Language: Slovenian
		$lang_string[ 'locale' ] = array('si_SI', 'si');
		$lang_string['language'] = 'slovenian';
		$lang_string['rss_locale'] = 'si_SI';

		// ISO Charset: ISO-8859-2
		$lang_string['html_charset'] = 'ISO-8859-2';
		$lang_string['php_charset'] = 'ISO-8859-2';

		setlocale( LC_TIME, $lang_string['locale'] );

	}
?>
