<?php

	// English Language File
	// (c) 2004 Alexander Palmo, apalmo <at> bigevilbrain <dot> com
	//

	// Simple PHP Version: 0.4.1
	// Language Version:   0.4.1.0	

	function sb_language( $page ="") {
		global $lang_string;			

		// Language: English
		$lang_string['language'] = 'românã';	
		
		$lang_string['locale'] = array('ro_RO', 'ro');
		$lang_string['rss_locale'] = 'ro-RO'; // New 0.4.8

		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-2';
		$lang_string['php_charset'] = 'ISO-8859-2';	

		setlocale( LC_TIME, $lang_string['locale'] );		

	}
?>
