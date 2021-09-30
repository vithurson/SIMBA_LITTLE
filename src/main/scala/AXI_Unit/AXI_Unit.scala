// See README.md for license details.

package AXI_Unit 

import chisel3._
import chisel3.util._
/**
  * Compute GCD using subtraction method.
  * Subtracts the smaller from the larger until register y is zero.
  * value in register x is then the GCD
_  */
class AXI_Unit extends Module {
        val io = IO( new Bundle{
       //writing
        val axi_awaddr  = Output(UInt(32.W))
        val axi_wdata   = Output(UInt(32.W))
        val axi_awready = Input(Bool())
        val axi_awvalid = Output(Bool())
        val axi_wvalid  = Output(Bool())
        val axi_wstrb   = Output(UInt(4.W))
        val axi_wready  = Input(Bool())
        /// write response
        val axi_bvalid  = Input(Bool())
        val axi_bready  = Output(Bool())
        val axi_bresp   = Input(UInt(2.W))

        // reading

        val axi_araddr  = Output(UInt(32.W))
        val axi_rdata   = Input(UInt(32.W))
        val axi_arready = Input(Bool())
        val axi_arvalid = Output(Bool())
        val axi_rvalid  = Input(Bool())
        val axi_rready  = Output(Bool())

        // 
        val control     = Input(UInt(2.W))
        val address     = Input(UInt(2.W))
        val wstrb_in    = Input(UInt(4.W))
        val data        = Input(UInt(32.W))
        val data_out    = Output(UInt(32.W))
        val done        = Output(Bool())
    })
    //write related regs
  val awaddr  = RegInit(UInt(32.W),0.U)
  val wdata   = RegInit(UInt(32.W),0.U)
  val wstrb   = RegInit(UInt(4.W),0.U)
  val wvalid  = RegInit(Bool(),0.B)
  val awvalid = RegInit(Bool(),0.B)

  //wresp
  val bready  = RegInit(Bool(),1.B)

  //read related regs
  val araddr  = RegInit(UInt(32.W),0.U)
  val arvalid = RegInit(Bool(),0.B)
  val rready  = RegInit(Bool(),0.U)

  /// tight interface
  val o_data  = RegInit(UInt(32.W),0.U)
  val done_i = RegInit(Bool(),1.B)
  val idle :: read :: write :: Nil = Enum(4)
  val aw_st :: w_st :: b_res :: Nil= Enum(4)
  val ar_st :: r_st :: Nil  = Enum(3)
  val state = RegInit(UInt(2.W),0.U)
  val wstate = RegInit(UInt(2.W),0.U)
  val rstate = RegInit(UInt(1.W),0.B)
  switch(state) {
      is(idle) {
          when(io.control===read) {
              state     := read
              done_i    := 0.U
          }
          .elsewhen(io.control === write){
              state     := write
              done_i    := 0.U
              wdata     := io.data
              wstrb     := io.wstrb_in
              awaddr    := io.address
              araddr    := io.axi_araddr
          }
      }
      is(write) {
          switch(wstate){
              is(aw_st){
                  when(io.axi_awready & awvalid){
                      awvalid   := 0.U
                      wstate    := w_st
                  }
                  .elsewhen(~awvalid){
                      awvalid := 1.U 
                  }

              }
              is(w_st){
                  when(wvalid & io.axi_wready){
                      wvalid := 0.U
                      wstate := b_res
                  }
              }
              is(b_res){
                  bready := io.axi_bvalid & ~bready
                  when(bready){
                    wstate := aw_st 
                    state  := idle
                    done_i := 1.U
                  }
              }
          }
      }
      is(read){
          switch(rstate){
              is(ar_st) {
                  when(io.axi_arready & arvalid){
                      arvalid:= 0.U
                      wstate := r_st
                  }
                  .elsewhen(~arvalid){
                      arvalid := 1.U 
                  }
              }
              is(r_st) {
                  rready := io.axi_rvalid & ~rready
                  when(bready){
                    rstate := ar_st 
                    state  := idle
                    done_i := 1.B
                    o_data := io.axi_rdata
                  }
              }
          }
                

      }
  }
  io.data_out   := o_data
  io.done       := done_i

  io.axi_awaddr := awaddr
  io.axi_wdata  := wdata 
  io.axi_wvalid := wvalid
  io.axi_awvalid := awvalid

  io.axi_araddr := araddr
  io.axi_araddr := araddr
  io.axi_rready := rready 
  io.axi_wstrb  := wstrb
}
