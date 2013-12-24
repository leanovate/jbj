<?php
	// Greek Translations
	// (c) 2004 Alexander Palmo, apalmo <at> bigevilbrain <dot> com (0.4.1)
	//     2006 update & revision by George Yiftoyiannis 
	
	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: Greek
		$lang_string['language'] = 'greek';
		
		$lang_string['locale'] = array('el_GR', 'grc');
		$lang_string['rss_locale'] = 'el-GR'; // New 0.4.8
		
		// ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'ISO-8859-7'; // is utf-8 out of the question ???
		$lang_string['php_charset'] = 'ISO-8859-7'; // ditto ...
		
		setlocale( LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}
		
?>
