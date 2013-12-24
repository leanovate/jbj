<?php
	// German Translation(s)
	// (c) 2004 Josef Angstenberger, jtxa <at> users <dot> sourceforge <dot> net (0.4.0)

	function sb_language( $page = "") {
		global $lang_string;

		// Language: German
		$lang_string['language'] = 'german';

		$lang_string[ 'locale' ] = array('de_DE','german');
		$lang_string['rss_locale'] = 'de-DE'; // New 0.4.8

		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-1';
		$lang_string['php_charset'] = 'ISO-8859-1';
		
		setlocale(LC_TIME, $lang_string['locale'] );

	}

?>
