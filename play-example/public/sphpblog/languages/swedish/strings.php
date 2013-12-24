<?php
	// Swedish Language Translation(s)
	// (c) 2004 Hans K Hard, hans <at> nikielhard <dot> se (0.4.1)

	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: Swedish
		$lang_string['language'] = 'swedish';
		
		$lang_string[ 'locale' ] = array('sv_SE','sve_SWE','swedish');
		$lang_string['rss_locale'] = 'sv-SE'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1 -- http://us4.php.net/manual/en/function.htmlspecialchars.php
		$lang_string['html_charset'] = 'ISO-8859-15';
		$lang_string['php_charset'] = 'ISO-8859-15';
		
		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}
		
?>
