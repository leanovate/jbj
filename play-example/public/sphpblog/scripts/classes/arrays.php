<?php
	/**
	* Additional array related utility functions.
	*
	* @author		Alexander Palmo <apalmo at bigevilbrain dot com>
	* @access		public static
	*
	* implode_key($arr, $delim='|')
	* explode_key($str, $delim='|')
	* implode_list($arr, $delimA='|', $delimB="\n")
	* explode_list($str, $delimA='|', $delimB="\n")
	*/
	class arrays {
		
		/**
		* Implode an associative array.
		*
		* Example Usage:
		* $str = arrays::implode_key(array("KEY1"=>"VALUE1"));
		*
		* @param		array $arr
		* @param		string $delim
		* @param		boolean $htmlize
		* @return		string
		*/
		function implode_key( $arr, $delim='|' ) {
			// Hex value of pipe is &#124;
			$hex = "&#".ord($delim).";";
			
			$arr_out = array();
			foreach ($arr as $key => $val) {
				$k = str_replace($delim, $hex, $key);
				$v = str_replace($delim, $hex, $val);
				array_push($arr_out, $k);
				array_push($arr_out, $v);
			}
			$str_out = implode($delim, $arr_out);
			
			return $str_out;
		}
		
		/**
		* Explode string to an associative array
		*
		* Example Usage:
		* $arr = arrays::explode_key("KEY1|VALUE1|KEY2|VALUE2");
		*
		* @param		string/array $str
		* @param		string $delim
		* @return		array
		*/
		function explode_key( $str, $delim='|' ) {
			// Hex value of pipe is &#124; and html-munged version is &amp;#124;
			$hex_arr = array('&#'.ord($delim).';', '&amp;#'.ord($delim).';');
							
			if ( is_array( $str ) ) {
				$arr = $str;
			} else {
				$arr = explode($delim, $str);
			}
			
			$arr_out = array();
			for ($i = 0; $i < count($arr); $i = $i + 2) {
				$k = str_replace($hex_arr, $delim, $arr[$i]);
				$v = str_replace($hex_arr, $delim, $arr[$i+1]);
				$arr_out[ $k ] = $v;
			}
			
			return $arr_out;
		}
		
		/**
		* Implode an array of associative arrays.
		*
		* Example Usage:
		* $arr = array( array("KEY"=>"VALUE1"), array("KEY"=>"VALUE2") );
		* $str = arrays::implode_list($arr);
		*
		* @param		array $arr
		* @param		string $delim
		* @param		boolean $htmlize
		* @return		string
		*/
		function implode_list( $arr, $delimA='|', $delimB="\n" ) {
			// $hexA = "&#".ord($delimA).";";
			$hexB = '&#'.ord($delimB).';';
			
			$arr_out = array();
			foreach ($arr as $val) {
				$v = str_replace($delimB, $hexB, $val);
				array_push($arr_out, arrays::implode_key($v, $delimA));
			}
			$str = implode($delimB, $arr_out);
			
			return $str;
		}
		
		/**
		* Explode string to an array of associative array
		*
		* Example Usage:
		* $arr = arrays::explode_key("KEY|VALUE1\nKEY|VALUE2&#10;SOMETHING"); // &#10; is hex for \n...
		*
		* @param		string $str
		* @param		string $delim
		* @return		array
		*/
		function explode_list( $str, $delimA='|', $delimB="\n" ) {
			// Hex value of pipe is &#124; and html-munged version is &amp;#124;
			$hex_arr = array('&#'.ord($delimB).';', '&amp;#'.ord($delimB).';');
			
			$arr = explode($delimB, $str);
			$arr_out = array();
			foreach ($arr as $val) {
				$v = str_replace($hex_arr, $delimB, $val);
				array_push($arr_out, arrays::explode_key($v, $delimA));
			}
			
			return $arr_out;
		}
	
	}
?>