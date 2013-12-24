<?php
  // Catalan  Language Translation(s)
  // (c) 2006 Laura Primo Monlleo laupri <at> gmail <dot> com  (0.4.8)
  //  Retocat per Montse Treviño mtrevim <at> gmail <dot> com  (0.5.1) [2008]

  // Language Version:   0.5.1

  function sb_language( $page ="") {
    global $language, $html_charset, $php_charset, $lang_string;

    // Language: Spanish
    $lang_string['language'] = 'catala';
    $lang_string[ 'locale' ] = array('ca_ES', 'ca', 'catala');
    $lang_string['rss_locale'] = 'ca-ES'; // New 0.4.8

    // ISO Charset: ISO-8859-1
    $lang_string['html_charset'] = 'ISO-8859-1';
    $lang_string['php_charset'] = 'ISO-8859-1';
    setlocale( LC_TIME, $lang_string['locale'] );

  }
?>
