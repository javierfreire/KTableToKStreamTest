package org.example

import kotlinx.cli.ArgParser
import kotlinx.cli.ExperimentalCli
import org.example.cli.SendTest
import org.example.cli.SpyOutput
import org.example.cli.StartApp

@ExperimentalCli
fun main(args: Array<String>) {
    val parser = ArgParser("kTableToKStreamTest:: 0.0.1")

    parser.subcommands(StartApp(), SendTest(), SpyOutput())

    parser.parse(args)
}