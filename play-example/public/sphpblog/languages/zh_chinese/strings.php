<?php
	// Traditional Chinese UTF-8 Language File
	// (c) 2004 Markmcm, mcha226 <at> hotmail <dot> com
	// (c) 2004 Judge Hu, judgehou <at> gmail <dot> com
	// Simple PHP Version: 0.4.6
	// Language Version:   0.4.1.1
	
	// Updated by NightCat for 0.7.0
	
	function sb_language( $page ="") {
		global $lang_string;
			
		// Language: Traditional Chinese
		$lang_string['language'] = 'chinese';
		
		$lang_string[ 'locale' ] = array('zh_TW.UTF-8','cht','chinese-traditional');
		$lang_string['rss_locale'] = 'zh-TW'; // New 0.4.8
		
		// ISO Charset: UTF-8
		$lang_string['html_charset'] = 'UTF-8';
		$lang_string['php_charset'] = 'UTF-8';		

		setlocale(LC_TIME, $lang_string['locale'] ); // <-- New 0.3.7
		
	}

?>
