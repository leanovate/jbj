<?php
	// Czech Language File
	// (c) 2006 Josef Klimosz, jklimosz <at> gmail <dot> com
	// for version 0.5.1 edited by (c) Lupyno, lupyno <at> seznam <dot> cz 

	// Simple PHP Version: 0.5.1
	// Czech Language Version: 0.5.1	

	function sb_language( $page ="") {
		global $lang_string;			

		// Language: Czech
		$lang_string['language'] = 'czech';
		$lang_string['locale'] = array('cs_CZ', 'cs','czech');
		$lang_string['rss_locale'] = 'cs-CZ'; // New 0.4.8

		// Windows Charset: CP1250
		$lang_string['html_charset'] = 'CP1250';
		$lang_string['php_charset'] = 'CP1250';
		setlocale( LC_TIME, $lang_string['locale'] );		

	}
?>
