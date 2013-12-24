<?php
	// Atom feed support
	// (c) 2004 Javier Gutiérrez Chamorro (Guti), guti <at> ya <dot> com
	//
	// Simple PHP Version: 0.8.0
	// Atom Version:	 0.8.0


	// Include Required Functions
	require_once('scripts/sb_functions.php');

	// Output Page
	generate_atom( @$_GET[ 'n' ], @$_GET[ 'c' ] );
?>
