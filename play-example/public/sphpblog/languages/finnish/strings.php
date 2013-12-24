<?php

	// Finnish Language File
	// (c) 2006 Niko Jakobsson, psycho <at> mbnet <dot> fi
	//

	// Simple PHP Version: 0.4.7
	// Language Version:   0.4.7.0
	

	function sb_language( $page ="") {
		global $lang_string;			

		// Language: Finnish
		$lang_string['language'] = 'finnish';
    $lang_string['locale'] = array('fi_FI', 'fi');
		$lang_string['rss_locale'] = 'fi-FI'; // New 0.4.8		

		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-1';
		$lang_string['php_charset'] = 'ISO-8859-1';		

		setlocale( LC_TIME, $lang_string['locale'] );		

	}
?>
