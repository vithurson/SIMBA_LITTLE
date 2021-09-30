package AXI_Unit
import chisel3._
import chisel3.tester._
import org.scalatest.FreeSpec

import chisel3.experimental.BundleLiterals._
class AXI_UnitSpec extends FreeSpec with ChiselScalatestTester {
    "ALU op generator Tester" in {
    test(new AXI_Unit()) { dut =>
  /**
    * Compute the gcd and the number of steps it should take to do it.
    *
    * @param a positive integer
    * @param b positive integer
    * @return the GCD of a and b, and how many steps it took
    */
      val len   =  20000
      val address = 0
      println("hiii")
      //val mem   =  Array[UInt](len)
      //def AXIdriver_slave(): Unit=  {
      //}
        
               dut.io.control.poke(2.U)
               dut.io.data.poke(143.U)
               dut.io.address.poke(143.U)
               dut.clock.step(1)
         while (dut.io.axi_awvalid.peek()==0.B){
               dut.clock.step(1)
          }
               dut.clock.step(1)
               dut.io.axi_awready.poke(1.B)
                println("address is ",dut.io.axi_awaddr)
               dut.clock.step(1)
               dut.io.axi_awready.poke(0.B)
    }
    }
}
