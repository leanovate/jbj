<?php
  // Bulgarian Language File
  // (c) 2004 Alexander Palmo, apalmo <at> bigevilbrain <dot> com 
  // Edits: 26.09.2007 Peicho Dimitrov info <at> alein <dot> org
  // Ако имам правописни грешки, поправете си ги ;)
  // Превода е на 98%

  // Simple PHP Version: 0.5.1
  // Language Version:   0.5.1

  function sb_language( $page ="") {
    global $lang_string;

    // Language: English
    $lang_string['language'] = 'bulgarian';
    $lang_string['locale'] = array('bg_BG', 'bulgaria', 'bul');
    $lang_string['rss_locale'] = 'bg-BG'; // New 0.4.8

    // ISO Charset: ISO-8859-1
		$lang_string['html_charset'] = 'windows-1251';
		$lang_string['php_charset'] = 'windows-1251';
    setlocale( LC_TIME, $lang_string['locale'] );

  }
?>
