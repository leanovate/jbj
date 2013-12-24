<?php

	// Slovak Language File
	// (c) 2009 palinux, admin <at> obchodp <dot> sk
	//

	// Simple PHP Version: 0.4.8
	// Slovak Language Version:   0.4.8.2	

	function sb_language( $page ="") {
		global $lang_string;			

		// Language: Slovak
		$lang_string['language'] = 'slovak';	
		
		$lang_string['locale'] = array('sk_SK', 'sk','slovak');
		$lang_string['rss_locale'] = 'sk-SK'; // New 0.4.8

		// Windows Charset: CP1250
		$lang_string['html_charset'] = 'CP1250';
		$lang_string['php_charset'] = 'CP1250';	

		setlocale( LC_TIME, $lang_string['locale'] );		

	}
?>
