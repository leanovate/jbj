<?php
	// French Language File
	// (c) 2004 Thibaud ChonŽ, thibaud <dot> chone <at> insa-lyon <dot> fr (0.3.7)
	// (c) 2005 Bill Bateman (0.4.6)
	// (c) 2006 Christian Clavet
	// (c) 2006 Clement Moulin (0.4.9)
	// (c) 2008 Francis Roux-Serret (0.5.2)


	function sb_language( $page ="") {
		global $lang_string;

		// Language: Francais
		$lang_string['language'] = 'français';
		$lang_string['locale'] = array('fr_FR','fra','french');
		$lang_string['rss_locale'] = 'fr-FR'; // New 0.4.8

		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-1';
		$lang_string['php_charset'] = 'ISO-8859-1';
		setlocale( LC_TIME, $lang_string['locale'] );

	}
?>
