/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.pcre.tests

import org.specs2.mutable.SpecificationWithJUnit

class BasicSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "PCRE" should {
    "abusing preg_match()" in {
      // ../php-src/ext/pcre/tests/001.phpt
      script(
        """<?php
          |
          |foreach (array('2006-05-13', '06-12-12', 'data: "12-Aug-87"') as $s) {
          |	var_dump(preg_match('~
          |		(?P<date>
          |		(?P<year>(\d{2})?\d\d) -
          |		(?P<month>(?:\d\d|[a-zA-Z]{2,3})) -
          |		(?P<day>[0-3]?\d))
          |	~x', $s, $m));
          |
          |	var_dump($m);
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(1)
          |array(10) {
          |  [0]=>
          |  string(10) "2006-05-13"
          |  ["date"]=>
          |  string(10) "2006-05-13"
          |  [1]=>
          |  string(10) "2006-05-13"
          |  ["year"]=>
          |  string(4) "2006"
          |  [2]=>
          |  string(4) "2006"
          |  [3]=>
          |  string(2) "20"
          |  ["month"]=>
          |  string(2) "05"
          |  [4]=>
          |  string(2) "05"
          |  ["day"]=>
          |  string(2) "13"
          |  [5]=>
          |  string(2) "13"
          |}
          |int(1)
          |array(10) {
          |  [0]=>
          |  string(8) "06-12-12"
          |  ["date"]=>
          |  string(8) "06-12-12"
          |  [1]=>
          |  string(8) "06-12-12"
          |  ["year"]=>
          |  string(2) "06"
          |  [2]=>
          |  string(2) "06"
          |  [3]=>
          |  string(0) ""
          |  ["month"]=>
          |  string(2) "12"
          |  [4]=>
          |  string(2) "12"
          |  ["day"]=>
          |  string(2) "12"
          |  [5]=>
          |  string(2) "12"
          |}
          |int(1)
          |array(10) {
          |  [0]=>
          |  string(8) "12-Aug-8"
          |  ["date"]=>
          |  string(8) "12-Aug-8"
          |  [1]=>
          |  string(8) "12-Aug-8"
          |  ["year"]=>
          |  string(2) "12"
          |  [2]=>
          |  string(2) "12"
          |  [3]=>
          |  string(0) ""
          |  ["month"]=>
          |  string(3) "Aug"
          |  [4]=>
          |  string(3) "Aug"
          |  ["day"]=>
          |  string(1) "8"
          |  [5]=>
          |  string(1) "8"
          |}
          |""".stripMargin
      )
    }

    "preg_* with bogus vals" in {
      // ../php-src/ext/pcre/tests/002.phpt
      script(
        """<?php
          |
          |var_dump(preg_match());
          |var_dump(preg_match_all());
          |var_dump(preg_match_all('//', '', $dummy, 0xdead));
          |
          |var_dump(preg_quote());
          |var_dump(preg_quote(''));
          |
          |var_dump(preg_replace('/(.)/', '${1}${1', 'abc'));
          |var_dump(preg_replace('/.++\d*+[/', 'for ($', 'abc'));
          |var_dump(preg_replace('/(.)/e', 'for ($', 'abc'));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: preg_match() expects at least 2 parameters, 0 given in /BasicSpec.inlinePhp on line 3
          |bool(false)
          |
          |Warning: preg_match_all() expects at least 2 parameters, 0 given in /BasicSpec.inlinePhp on line 4
          |bool(false)
          |
          |Warning: preg_match_all(): Invalid flags specified in /BasicSpec.inlinePhp on line 5
          |NULL
          |
          |Warning: preg_quote() expects at least 1 parameter, 0 given in /BasicSpec.inlinePhp on line 7
          |NULL
          |string(0) ""
          |string(12) "a${1b${1c${1"
          |
          |Warning: preg_replace(): Compilation failed: Unclosed character class at offset 7 in /BasicSpec.inlinePhp on line 11
          |NULL
          |
          |Deprecated: preg_replace(): The /e modifier is deprecated, use preg_replace_callback instead in /BasicSpec.inlinePhp on line 12
          |
          |Fatal error: preg_replace(): Failed evaluating code: for ($ in /BasicSpec.inlinePhp on line 12
          |""".stripMargin
      )
    }

    "abusing preg_match_all()" in {
      // ../php-src/ext/pcre/tests/003.phpt
      script(
        """<?php
          |
          |foreach (array(PREG_PATTERN_ORDER, PREG_SET_ORDER) as $flag) {
          |	var_dump(preg_match_all('~
          |		(?P<date>
          |		(?P<year>(\d{2})?\d\d) -
          |		(?P<month>(?:\d\d|[a-zA-Z]{2,3})) -
          |		(?P<day>[0-3]?\d))
          |		~x',
          |		'2006-05-13 e outra data: "12-Aug-37"', $m, $flag));
          |
          |	var_dump($m);
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(2)
          |array(10) {
          |  [0]=>
          |  array(2) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |  }
          |  ["date"]=>
          |  array(2) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |  }
          |  [1]=>
          |  array(2) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |  }
          |  ["year"]=>
          |  array(2) {
          |    [0]=>
          |    string(4) "2006"
          |    [1]=>
          |    string(2) "12"
          |  }
          |  [2]=>
          |  array(2) {
          |    [0]=>
          |    string(4) "2006"
          |    [1]=>
          |    string(2) "12"
          |  }
          |  [3]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "20"
          |    [1]=>
          |    string(0) ""
          |  }
          |  ["month"]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "05"
          |    [1]=>
          |    string(3) "Aug"
          |  }
          |  [4]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "05"
          |    [1]=>
          |    string(3) "Aug"
          |  }
          |  ["day"]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "13"
          |    [1]=>
          |    string(2) "37"
          |  }
          |  [5]=>
          |  array(2) {
          |    [0]=>
          |    string(2) "13"
          |    [1]=>
          |    string(2) "37"
          |  }
          |}
          |int(2)
          |array(2) {
          |  [0]=>
          |  array(10) {
          |    [0]=>
          |    string(10) "2006-05-13"
          |    ["date"]=>
          |    string(10) "2006-05-13"
          |    [1]=>
          |    string(10) "2006-05-13"
          |    ["year"]=>
          |    string(4) "2006"
          |    [2]=>
          |    string(4) "2006"
          |    [3]=>
          |    string(2) "20"
          |    ["month"]=>
          |    string(2) "05"
          |    [4]=>
          |    string(2) "05"
          |    ["day"]=>
          |    string(2) "13"
          |    [5]=>
          |    string(2) "13"
          |  }
          |  [1]=>
          |  array(10) {
          |    [0]=>
          |    string(9) "12-Aug-37"
          |    ["date"]=>
          |    string(9) "12-Aug-37"
          |    [1]=>
          |    string(9) "12-Aug-37"
          |    ["year"]=>
          |    string(2) "12"
          |    [2]=>
          |    string(2) "12"
          |    [3]=>
          |    string(0) ""
          |    ["month"]=>
          |    string(3) "Aug"
          |    [4]=>
          |    string(3) "Aug"
          |    ["day"]=>
          |    string(2) "37"
          |    [5]=>
          |    string(2) "37"
          |  }
          |}
          |""".stripMargin
      )
    }

    "abusing pcre" in {
      // ../php-src/ext/pcre/tests/004.phpt
      script(
        """<?php
          |
          |var_dump(preg_match_all('/((?:(?:unsigned|struct)\s+)?\w+)(?:\s*(\*+)\s+|\s+(\**))(\w+(?:\[\s*\w*\s*\])?)\s*(?:(=)[^,;]+)?((?:\s*,\s*\**\s*\w+(?:\[\s*\w*\s*\])?\s*(?:=[^,;]+)?)*)\s*;/S', 'unsigned int xpto = 124; short a, b;', $m, PREG_SET_ORDER));
          |var_dump($m);
          |
          |var_dump(preg_match_all('/(?:\([^)]+\))?(&?)([\w>.()-]+(?:\[\w+\])?)\s*,?((?:\)*\s*=)?)/S', '&a, b, &c', $m, PREG_SET_ORDER));
          |var_dump($m);
          |
          |var_dump(preg_match_all('/zend_parse_parameters(?:_ex\s*\([^,]+,[^,]+|\s*\([^,]+),\s*"([^"]*)"\s*,\s*([^{;]*)/S', 'zend_parse_parameters( 0, "addd|s/", a, b, &c);', $m, PREG_SET_ORDER | PREG_OFFSET_CAPTURE));
          |var_dump($m);
          |
          |var_dump(preg_replace(array('@//.*@S', '@/\*.*\*/@SsUe'), array('', 'preg_replace("/[^\r\n]+/S", "", \'$0\')'), "hello\n//x \n/*\ns\n*/"));
          |
          |var_dump(preg_split('/PHP_(?:NAMED_)?(?:FUNCTION|METHOD)\s*\((\w+(?:,\s*\w+)?)\)/S', "PHP_FUNCTION(s, preg_match)\n{\nlalala", -1, PREG_SPLIT_DELIM_CAPTURE | PREG_SPLIT_OFFSET_CAPTURE));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(2)
          |array(2) {
          |  [0]=>
          |  array(7) {
          |    [0]=>
          |    string(24) "unsigned int xpto = 124;"
          |    [1]=>
          |    string(12) "unsigned int"
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(4) "xpto"
          |    [5]=>
          |    string(1) "="
          |    [6]=>
          |    string(0) ""
          |  }
          |  [1]=>
          |  array(7) {
          |    [0]=>
          |    string(11) "short a, b;"
          |    [1]=>
          |    string(5) "short"
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(1) "a"
          |    [5]=>
          |    string(0) ""
          |    [6]=>
          |    string(3) ", b"
          |  }
          |}
          |int(3)
          |array(3) {
          |  [0]=>
          |  array(4) {
          |    [0]=>
          |    string(3) "&a,"
          |    [1]=>
          |    string(1) "&"
          |    [2]=>
          |    string(1) "a"
          |    [3]=>
          |    string(0) ""
          |  }
          |  [1]=>
          |  array(4) {
          |    [0]=>
          |    string(2) "b,"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(1) "b"
          |    [3]=>
          |    string(0) ""
          |  }
          |  [2]=>
          |  array(4) {
          |    [0]=>
          |    string(2) "&c"
          |    [1]=>
          |    string(1) "&"
          |    [2]=>
          |    string(1) "c"
          |    [3]=>
          |    string(0) ""
          |  }
          |}
          |int(1)
          |array(1) {
          |  [0]=>
          |  array(3) {
          |    [0]=>
          |    array(2) {
          |      [0]=>
          |      string(46) "zend_parse_parameters( 0, "addd|s/", a, b, &c)"
          |      [1]=>
          |      int(0)
          |    }
          |    [1]=>
          |    array(2) {
          |      [0]=>
          |      string(7) "addd|s/"
          |      [1]=>
          |      int(27)
          |    }
          |    [2]=>
          |    array(2) {
          |      [0]=>
          |      string(9) "a, b, &c)"
          |      [1]=>
          |      int(37)
          |    }
          |  }
          |}
          |
          |Deprecated: preg_replace(): The /e modifier is deprecated, use preg_replace_callback instead in /BasicSpec.inlinePhp on line 12
          |string(9) "hello
          |
          |
          |
          |"
          |array(3) {
          |  [0]=>
          |  array(2) {
          |    [0]=>
          |    string(0) ""
          |    [1]=>
          |    int(0)
          |  }
          |  [1]=>
          |  array(2) {
          |    [0]=>
          |    string(13) "s, preg_match"
          |    [1]=>
          |    int(13)
          |  }
          |  [2]=>
          |  array(2) {
          |    [0]=>
          |    string(9) "
          |{
          |lalala"
          |    [1]=>
          |    int(27)
          |  }
          |}
          |""".stripMargin
      )
    }

    "abusing preg_match_all() #2" in {
      // ../php-src/ext/pcre/tests/005.phpt
      script(
        """<?php
          |// this file is not used in the cron job
          |// use it to test the gcc regex with the sample data provided
          |
          |$sampledata = "
          |/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c: In function `zif_swfbitmap_init':
          |/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c:323: warning: assignment from incompatible pointer type
          |/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c: In function `zif_swftextfield_setFont':
          |/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c:2597: warning: passing arg 2 of `SWFTextField_setFont' from incompatible pointer type
          |/p2/var/php_gcov/PHP_4_4/ext/oci8/oci8.c:1027: warning: `oci_ping' defined but not used
          |/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c: In function `zif_posix_getpgid':
          |/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c:484: warning: implicit declaration of function `getpgid'
          |/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c: In function `zif_posix_getsid':
          |/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c:506: warning: implicit declaration of function `getsid'
          |/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c: In function `ps_read_files':
          |/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c:302: warning: implicit declaration of function `pread'
          |/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c: In function `ps_write_files':
          |/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c:340: warning: implicit declaration of function `pwrite'
          |/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c: In function `zif_socket_get_option':
          |/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c:1862: warning: unused variable `timeout'
          |/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c: In function `zif_socket_set_option':
          |/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c:1941: warning: unused variable `timeout'
          |/p2/var/php_gcov/PHP_4_4/regex/regexec.c:19: warning: `nope' defined but not used
          |/p2/var/php_gcov/PHP_4_4/ext/standard/exec.c:50: warning: `php_make_safe_mode_command' defined but not used
          |/p2/var/php_gcov/PHP_4_4/ext/standard/image.c: In function `php_handle_jpc':
          |/p2/var/php_gcov/PHP_4_4/ext/standard/image.c:604: warning: unused variable `dummy_int'
          |/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c: In function `php_gd_parse':
          |/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c:1138: warning: implicit declaration of function `php_gd_lex'
          |/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.y: At top level:
          |/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.y:864: warning: return type defaults to `int'
          |/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c: In function `zif_msg_receive':
          |/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c:318: warning: passing arg 2 of `php_var_unserialize' from incompatible pointer type
          |/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c: In function `zif_yp_err_string':
          |/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c:372: warning: assignment discards qualifiers from pointer target type
          |Zend/zend_language_scanner.c:5944: warning: `yy_fatal_error' defined but not used
          |Zend/zend_language_scanner.c:2627: warning: `yy_last_accepting_state' defined but not used
          |Zend/zend_language_scanner.c:2628: warning: `yy_last_accepting_cpos' defined but not used
          |Zend/zend_language_scanner.c:2634: warning: `yy_more_flag' defined but not used
          |Zend/zend_language_scanner.c:2635: warning: `yy_more_len' defined but not used
          |Zend/zend_language_scanner.c:5483: warning: `yyunput' defined but not used
          |Zend/zend_language_scanner.c:5929: warning: `yy_top_state' defined but not used
          |conflicts: 2 shift/reduce
          |Zend/zend_ini_scanner.c:457: warning: `yy_last_accepting_state' defined but not used
          |Zend/zend_ini_scanner.c:458: warning: `yy_last_accepting_cpos' defined but not used
          |Zend/zend_ini_scanner.c:1361: warning: `yyunput' defined but not used
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c: In function `_safe_emalloc':
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c:237: warning: long int format, size_t arg (arg 3)
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c:237: warning: long int format, size_t arg (arg 4)
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c:237: warning: long int format, size_t arg (arg 5)
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_ini.c:338: warning: `zend_ini_displayer_cb' defined but not used
          |ext/mysql/libmysql/my_tempnam.o(.text+0x80): In function `my_tempnam':
          |/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c:115: warning: the use of `tempnam' is dangerous, better use `mkstemp'
          |ext/mysql/libmysql/my_tempnam.o(.text+0x80): In function `my_tempnam':
          |/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c:115: warning: the use of `tempnam' is dangerous, better use `mkstemp'
          |ext/ming/ming.o(.text+0xc115): In function `zim_swfmovie_namedAnchor':
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:2207: undefined reference to `SWFMovie_namedAnchor'
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:2209: undefined reference to `SWFMovie_xpto'
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:2259: undefined reference to `SWFMovie_foo'
          |ext/ming/ming.o(.text+0x851): In function `zif_ming_setSWFCompression':
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:154: undefined reference to `Ming_setSWFCompression'
          |";
          |
          |    // Regular expression to select the error and warning information
          |    // tuned for gcc 3.4, 4.0 and 4.1
          |    $gcc_regex = '/^((.+)(\(\.text\+0x[[:xdigit:]]+\))?: In function [`\'](\w+)\':\s+)?'.
          |        '((([^:\n]+|\2)|[^:\n]+)):(\d+): (?:(error|warning):\s+)?(.+)'.
          |        str_repeat('(?:\s+\5:(\d+): (?:(error|warning):\s+)?(.+))?', 99). // capture up to 100 errors
          |        '/mS';
          |
          |
          |var_dump(preg_match_all($gcc_regex, $sampledata, $m, PREG_SET_ORDER));
          |var_dump($m);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(24)
          |array(24) {
          |  [0]=>
          |  array(11) {
          |    [0]=>
          |    string(172) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c: In function `zif_swfbitmap_init':
          |/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c:323: warning: assignment from incompatible pointer type"
          |    [1]=>
          |    string(76) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c: In function `zif_swfbitmap_init':
          |"
          |    [2]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(18) "zif_swfbitmap_init"
          |    [5]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [6]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [7]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [8]=>
          |    string(3) "323"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(41) "assignment from incompatible pointer type"
          |  }
          |  [1]=>
          |  array(11) {
          |    [0]=>
          |    string(208) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c: In function `zif_swftextfield_setFont':
          |/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c:2597: warning: passing arg 2 of `SWFTextField_setFont' from incompatible pointer type"
          |    [1]=>
          |    string(82) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c: In function `zif_swftextfield_setFont':
          |"
          |    [2]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(24) "zif_swftextfield_setFont"
          |    [5]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [6]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [7]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/ming/ming.c"
          |    [8]=>
          |    string(4) "2597"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(70) "passing arg 2 of `SWFTextField_setFont' from incompatible pointer type"
          |  }
          |  [2]=>
          |  array(11) {
          |    [0]=>
          |    string(87) "/p2/var/php_gcov/PHP_4_4/ext/oci8/oci8.c:1027: warning: `oci_ping' defined but not used"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(0) ""
          |    [5]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/oci8/oci8.c"
          |    [6]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/oci8/oci8.c"
          |    [7]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/ext/oci8/oci8.c"
          |    [8]=>
          |    string(4) "1027"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(31) "`oci_ping' defined but not used"
          |  }
          |  [3]=>
          |  array(11) {
          |    [0]=>
          |    string(176) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c: In function `zif_posix_getpgid':
          |/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c:484: warning: implicit declaration of function `getpgid'"
          |    [1]=>
          |    string(77) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c: In function `zif_posix_getpgid':
          |"
          |    [2]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(17) "zif_posix_getpgid"
          |    [5]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [6]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [7]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [8]=>
          |    string(3) "484"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(42) "implicit declaration of function `getpgid'"
          |  }
          |  [4]=>
          |  array(11) {
          |    [0]=>
          |    string(174) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c: In function `zif_posix_getsid':
          |/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c:506: warning: implicit declaration of function `getsid'"
          |    [1]=>
          |    string(76) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c: In function `zif_posix_getsid':
          |"
          |    [2]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(16) "zif_posix_getsid"
          |    [5]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [6]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [7]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/ext/posix/posix.c"
          |    [8]=>
          |    string(3) "506"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(41) "implicit declaration of function `getsid'"
          |  }
          |  [5]=>
          |  array(11) {
          |    [0]=>
          |    string(182) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c: In function `ps_read_files':
          |/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c:302: warning: implicit declaration of function `pread'"
          |    [1]=>
          |    string(79) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c: In function `ps_read_files':
          |"
          |    [2]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(13) "ps_read_files"
          |    [5]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [6]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [7]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [8]=>
          |    string(3) "302"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(40) "implicit declaration of function `pread'"
          |  }
          |  [6]=>
          |  array(11) {
          |    [0]=>
          |    string(184) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c: In function `ps_write_files':
          |/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c:340: warning: implicit declaration of function `pwrite'"
          |    [1]=>
          |    string(80) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c: In function `ps_write_files':
          |"
          |    [2]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(14) "ps_write_files"
          |    [5]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [6]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [7]=>
          |    string(48) "/p2/var/php_gcov/PHP_4_4/ext/session/mod_files.c"
          |    [8]=>
          |    string(3) "340"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(41) "implicit declaration of function `pwrite'"
          |  }
          |  [7]=>
          |  array(11) {
          |    [0]=>
          |    string(172) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c: In function `zif_socket_get_option':
          |/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c:1862: warning: unused variable `timeout'"
          |    [1]=>
          |    string(85) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c: In function `zif_socket_get_option':
          |"
          |    [2]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(21) "zif_socket_get_option"
          |    [5]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [6]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [7]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [8]=>
          |    string(4) "1862"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(25) "unused variable `timeout'"
          |  }
          |  [8]=>
          |  array(11) {
          |    [0]=>
          |    string(172) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c: In function `zif_socket_set_option':
          |/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c:1941: warning: unused variable `timeout'"
          |    [1]=>
          |    string(85) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c: In function `zif_socket_set_option':
          |"
          |    [2]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(21) "zif_socket_set_option"
          |    [5]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [6]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [7]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sockets/sockets.c"
          |    [8]=>
          |    string(4) "1941"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(25) "unused variable `timeout'"
          |  }
          |  [9]=>
          |  array(11) {
          |    [0]=>
          |    string(81) "/p2/var/php_gcov/PHP_4_4/regex/regexec.c:19: warning: `nope' defined but not used"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(0) ""
          |    [5]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/regex/regexec.c"
          |    [6]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/regex/regexec.c"
          |    [7]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/regex/regexec.c"
          |    [8]=>
          |    string(2) "19"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(27) "`nope' defined but not used"
          |  }
          |  [10]=>
          |  array(11) {
          |    [0]=>
          |    string(107) "/p2/var/php_gcov/PHP_4_4/ext/standard/exec.c:50: warning: `php_make_safe_mode_command' defined but not used"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(0) ""
          |    [5]=>
          |    string(44) "/p2/var/php_gcov/PHP_4_4/ext/standard/exec.c"
          |    [6]=>
          |    string(44) "/p2/var/php_gcov/PHP_4_4/ext/standard/exec.c"
          |    [7]=>
          |    string(44) "/p2/var/php_gcov/PHP_4_4/ext/standard/exec.c"
          |    [8]=>
          |    string(2) "50"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(49) "`php_make_safe_mode_command' defined but not used"
          |  }
          |  [11]=>
          |  array(11) {
          |    [0]=>
          |    string(164) "/p2/var/php_gcov/PHP_4_4/ext/standard/image.c: In function `php_handle_jpc':
          |/p2/var/php_gcov/PHP_4_4/ext/standard/image.c:604: warning: unused variable `dummy_int'"
          |    [1]=>
          |    string(77) "/p2/var/php_gcov/PHP_4_4/ext/standard/image.c: In function `php_handle_jpc':
          |"
          |    [2]=>
          |    string(45) "/p2/var/php_gcov/PHP_4_4/ext/standard/image.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(14) "php_handle_jpc"
          |    [5]=>
          |    string(45) "/p2/var/php_gcov/PHP_4_4/ext/standard/image.c"
          |    [6]=>
          |    string(45) "/p2/var/php_gcov/PHP_4_4/ext/standard/image.c"
          |    [7]=>
          |    string(45) "/p2/var/php_gcov/PHP_4_4/ext/standard/image.c"
          |    [8]=>
          |    string(3) "604"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(27) "unused variable `dummy_int'"
          |  }
          |  [12]=>
          |  array(11) {
          |    [0]=>
          |    string(189) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c: In function `php_gd_parse':
          |/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c:1138: warning: implicit declaration of function `php_gd_lex'"
          |    [1]=>
          |    string(79) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c: In function `php_gd_parse':
          |"
          |    [2]=>
          |    string(49) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(12) "php_gd_parse"
          |    [5]=>
          |    string(49) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c"
          |    [6]=>
          |    string(49) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c"
          |    [7]=>
          |    string(49) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.c"
          |    [8]=>
          |    string(4) "1138"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(45) "implicit declaration of function `php_gd_lex'"
          |  }
          |  [13]=>
          |  array(11) {
          |    [0]=>
          |    string(93) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.y:864: warning: return type defaults to `int'"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(0) ""
          |    [5]=>
          |    string(49) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.y"
          |    [6]=>
          |    string(49) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.y"
          |    [7]=>
          |    string(49) "/p2/var/php_gcov/PHP_4_4/ext/standard/parsedate.y"
          |    [8]=>
          |    string(3) "864"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(29) "return type defaults to `int'"
          |  }
          |  [14]=>
          |  array(11) {
          |    [0]=>
          |    string(209) "/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c: In function `zif_msg_receive':
          |/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c:318: warning: passing arg 2 of `php_var_unserialize' from incompatible pointer type"
          |    [1]=>
          |    string(79) "/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c: In function `zif_msg_receive':
          |"
          |    [2]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(15) "zif_msg_receive"
          |    [5]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c"
          |    [6]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c"
          |    [7]=>
          |    string(46) "/p2/var/php_gcov/PHP_4_4/ext/sysvmsg/sysvmsg.c"
          |    [8]=>
          |    string(3) "318"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(69) "passing arg 2 of `php_var_unserialize' from incompatible pointer type"
          |  }
          |  [15]=>
          |  array(11) {
          |    [0]=>
          |    string(177) "/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c: In function `zif_yp_err_string':
          |/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c:372: warning: assignment discards qualifiers from pointer target type"
          |    [1]=>
          |    string(71) "/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c: In function `zif_yp_err_string':
          |"
          |    [2]=>
          |    string(36) "/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(17) "zif_yp_err_string"
          |    [5]=>
          |    string(36) "/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c"
          |    [6]=>
          |    string(36) "/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c"
          |    [7]=>
          |    string(36) "/p2/var/php_gcov/PHP_4_4/ext/yp/yp.c"
          |    [8]=>
          |    string(3) "372"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(55) "assignment discards qualifiers from pointer target type"
          |  }
          |  [16]=>
          |  array(29) {
          |    [0]=>
          |    string(576) "Zend/zend_language_scanner.c:5944: warning: `yy_fatal_error' defined but not used
          |Zend/zend_language_scanner.c:2627: warning: `yy_last_accepting_state' defined but not used
          |Zend/zend_language_scanner.c:2628: warning: `yy_last_accepting_cpos' defined but not used
          |Zend/zend_language_scanner.c:2634: warning: `yy_more_flag' defined but not used
          |Zend/zend_language_scanner.c:2635: warning: `yy_more_len' defined but not used
          |Zend/zend_language_scanner.c:5483: warning: `yyunput' defined but not used
          |Zend/zend_language_scanner.c:5929: warning: `yy_top_state' defined but not used"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(0) ""
          |    [5]=>
          |    string(28) "Zend/zend_language_scanner.c"
          |    [6]=>
          |    string(28) "Zend/zend_language_scanner.c"
          |    [7]=>
          |    string(28) "Zend/zend_language_scanner.c"
          |    [8]=>
          |    string(4) "5944"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(37) "`yy_fatal_error' defined but not used"
          |    [11]=>
          |    string(4) "2627"
          |    [12]=>
          |    string(7) "warning"
          |    [13]=>
          |    string(46) "`yy_last_accepting_state' defined but not used"
          |    [14]=>
          |    string(4) "2628"
          |    [15]=>
          |    string(7) "warning"
          |    [16]=>
          |    string(45) "`yy_last_accepting_cpos' defined but not used"
          |    [17]=>
          |    string(4) "2634"
          |    [18]=>
          |    string(7) "warning"
          |    [19]=>
          |    string(35) "`yy_more_flag' defined but not used"
          |    [20]=>
          |    string(4) "2635"
          |    [21]=>
          |    string(7) "warning"
          |    [22]=>
          |    string(34) "`yy_more_len' defined but not used"
          |    [23]=>
          |    string(4) "5483"
          |    [24]=>
          |    string(7) "warning"
          |    [25]=>
          |    string(30) "`yyunput' defined but not used"
          |    [26]=>
          |    string(4) "5929"
          |    [27]=>
          |    string(7) "warning"
          |    [28]=>
          |    string(35) "`yy_top_state' defined but not used"
          |  }
          |  [17]=>
          |  array(17) {
          |    [0]=>
          |    string(238) "Zend/zend_ini_scanner.c:457: warning: `yy_last_accepting_state' defined but not used
          |Zend/zend_ini_scanner.c:458: warning: `yy_last_accepting_cpos' defined but not used
          |Zend/zend_ini_scanner.c:1361: warning: `yyunput' defined but not used"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(0) ""
          |    [5]=>
          |    string(23) "Zend/zend_ini_scanner.c"
          |    [6]=>
          |    string(23) "Zend/zend_ini_scanner.c"
          |    [7]=>
          |    string(23) "Zend/zend_ini_scanner.c"
          |    [8]=>
          |    string(3) "457"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(46) "`yy_last_accepting_state' defined but not used"
          |    [11]=>
          |    string(3) "458"
          |    [12]=>
          |    string(7) "warning"
          |    [13]=>
          |    string(45) "`yy_last_accepting_cpos' defined but not used"
          |    [14]=>
          |    string(4) "1361"
          |    [15]=>
          |    string(7) "warning"
          |    [16]=>
          |    string(30) "`yyunput' defined but not used"
          |  }
          |  [18]=>
          |  array(17) {
          |    [0]=>
          |    string(351) "/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c: In function `_safe_emalloc':
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c:237: warning: long int format, size_t arg (arg 3)
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c:237: warning: long int format, size_t arg (arg 4)
          |/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c:237: warning: long int format, size_t arg (arg 5)"
          |    [1]=>
          |    string(73) "/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c: In function `_safe_emalloc':
          |"
          |    [2]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(13) "_safe_emalloc"
          |    [5]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c"
          |    [6]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c"
          |    [7]=>
          |    string(42) "/p2/var/php_gcov/PHP_4_4/Zend/zend_alloc.c"
          |    [8]=>
          |    string(3) "237"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(35) "long int format, size_t arg (arg 3)"
          |    [11]=>
          |    string(3) "237"
          |    [12]=>
          |    string(7) "warning"
          |    [13]=>
          |    string(35) "long int format, size_t arg (arg 4)"
          |    [14]=>
          |    string(3) "237"
          |    [15]=>
          |    string(7) "warning"
          |    [16]=>
          |    string(35) "long int format, size_t arg (arg 5)"
          |  }
          |  [19]=>
          |  array(11) {
          |    [0]=>
          |    string(99) "/p2/var/php_gcov/PHP_4_4/Zend/zend_ini.c:338: warning: `zend_ini_displayer_cb' defined but not used"
          |    [1]=>
          |    string(0) ""
          |    [2]=>
          |    string(0) ""
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(0) ""
          |    [5]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/Zend/zend_ini.c"
          |    [6]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/Zend/zend_ini.c"
          |    [7]=>
          |    string(40) "/p2/var/php_gcov/PHP_4_4/Zend/zend_ini.c"
          |    [8]=>
          |    string(3) "338"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(44) "`zend_ini_displayer_cb' defined but not used"
          |  }
          |  [20]=>
          |  array(11) {
          |    [0]=>
          |    string(197) "ext/mysql/libmysql/my_tempnam.o(.text+0x80): In function `my_tempnam':
          |/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c:115: warning: the use of `tempnam' is dangerous, better use `mkstemp'"
          |    [1]=>
          |    string(71) "ext/mysql/libmysql/my_tempnam.o(.text+0x80): In function `my_tempnam':
          |"
          |    [2]=>
          |    string(43) "ext/mysql/libmysql/my_tempnam.o(.text+0x80)"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(10) "my_tempnam"
          |    [5]=>
          |    string(56) "/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c"
          |    [6]=>
          |    string(56) "/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c"
          |    [7]=>
          |    string(56) "/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c"
          |    [8]=>
          |    string(3) "115"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(55) "the use of `tempnam' is dangerous, better use `mkstemp'"
          |  }
          |  [21]=>
          |  array(11) {
          |    [0]=>
          |    string(197) "ext/mysql/libmysql/my_tempnam.o(.text+0x80): In function `my_tempnam':
          |/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c:115: warning: the use of `tempnam' is dangerous, better use `mkstemp'"
          |    [1]=>
          |    string(71) "ext/mysql/libmysql/my_tempnam.o(.text+0x80): In function `my_tempnam':
          |"
          |    [2]=>
          |    string(43) "ext/mysql/libmysql/my_tempnam.o(.text+0x80)"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(10) "my_tempnam"
          |    [5]=>
          |    string(56) "/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c"
          |    [6]=>
          |    string(56) "/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c"
          |    [7]=>
          |    string(56) "/p2/var/php_gcov/PHP_4_4/ext/mysql/libmysql/my_tempnam.c"
          |    [8]=>
          |    string(3) "115"
          |    [9]=>
          |    string(7) "warning"
          |    [10]=>
          |    string(55) "the use of `tempnam' is dangerous, better use `mkstemp'"
          |  }
          |  [22]=>
          |  array(17) {
          |    [0]=>
          |    string(334) "ext/ming/ming.o(.text+0xc115): In function `zim_swfmovie_namedAnchor':
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:2207: undefined reference to `SWFMovie_namedAnchor'
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:2209: undefined reference to `SWFMovie_xpto'
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:2259: undefined reference to `SWFMovie_foo'"
          |    [1]=>
          |    string(71) "ext/ming/ming.o(.text+0xc115): In function `zim_swfmovie_namedAnchor':
          |"
          |    [2]=>
          |    string(29) "ext/ming/ming.o(.text+0xc115)"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(24) "zim_swfmovie_namedAnchor"
          |    [5]=>
          |    string(40) "/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c"
          |    [6]=>
          |    string(40) "/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c"
          |    [7]=>
          |    string(40) "/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c"
          |    [8]=>
          |    string(4) "2207"
          |    [9]=>
          |    string(0) ""
          |    [10]=>
          |    string(45) "undefined reference to `SWFMovie_namedAnchor'"
          |    [11]=>
          |    string(4) "2209"
          |    [12]=>
          |    string(0) ""
          |    [13]=>
          |    string(38) "undefined reference to `SWFMovie_xpto'"
          |    [14]=>
          |    string(4) "2259"
          |    [15]=>
          |    string(0) ""
          |    [16]=>
          |    string(37) "undefined reference to `SWFMovie_foo'"
          |  }
          |  [23]=>
          |  array(11) {
          |    [0]=>
          |    string(165) "ext/ming/ming.o(.text+0x851): In function `zif_ming_setSWFCompression':
          |/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c:154: undefined reference to `Ming_setSWFCompression'"
          |    [1]=>
          |    string(72) "ext/ming/ming.o(.text+0x851): In function `zif_ming_setSWFCompression':
          |"
          |    [2]=>
          |    string(28) "ext/ming/ming.o(.text+0x851)"
          |    [3]=>
          |    string(0) ""
          |    [4]=>
          |    string(26) "zif_ming_setSWFCompression"
          |    [5]=>
          |    string(40) "/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c"
          |    [6]=>
          |    string(40) "/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c"
          |    [7]=>
          |    string(40) "/p2/var/php_gcov/PHP_5_2/ext/ming/ming.c"
          |    [8]=>
          |    string(3) "154"
          |    [9]=>
          |    string(0) ""
          |    [10]=>
          |    string(47) "undefined reference to `Ming_setSWFCompression'"
          |  }
          |}
          |""".stripMargin
      )
    }

    "preg_replace_callback() with callback that modifies subject string" in {
      // ../php-src/ext/pcre/tests/007.phpt
      script(
        """<?php
          |
          |function evil($x) {
          |	global $txt;
          |	$txt[3] = "\xFF";
          |	var_dump($x);
          |	return $x[0];
          |}
          |
          |$txt = "ola123";
          |var_dump(preg_replace_callback('#.#u', 'evil', $txt));
          |var_dump($txt);
          |var_dump(preg_last_error() == PREG_NO_ERROR);
          |
          |echo "Done!\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(1) {
          |  [0]=>
          |  string(1) "o"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "l"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "a"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "1"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "2"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "3"
          |}
          |string(6) "ola123"
          |string(6) "ola�23"
          |bool(true)
          |Done!
          |""".stripMargin
      )
    }
  }
}
