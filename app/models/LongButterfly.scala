package models

/**
 * Long Butterfly is purchasing 1 ITM, 1 OTM, and selling 2 ATM - same as a BullCall + BearCall 
 * (or BullPut + BearPut) with identical short strikes and equal distance between short and long strikes 
 */
abstract class LongButterfly(lowerTwoLeg: TwoLegTrade, higherTwoLeg: TwoLegTrade) 
	extends FourLegTrade(lowerTwoLeg.underlier, lowerTwoLeg.undLast, lowerTwoLeg.expires) with Rangebound {

  val comparator = lowerTwoLeg.comparator + higherTwoLeg.comparator
  val maxLossAmount = lowerTwoLeg.longAsk + higherTwoLeg.longAsk - lowerTwoLeg.shortBid - higherTwoLeg.shortBid
  val maxProfitAmount = lowerTwoLeg.higherStrike - lowerTwoLeg.lowerStrike - maxLossAmount
  val maxProfitPrice = lowerTwoLeg.shortStrike
  val lowerMaxLossPrice = lowerTwoLeg.lowerStrike + maxLossAmount
  val higherMaxLossPrice = higherTwoLeg.higherStrike - maxLossAmount
  val maxLossPrice = if ((undLast - lowerMaxLossPrice) < (higherMaxLossPrice - undLast)) {
    lowerMaxLossPrice
  } else {
    higherMaxLossPrice
  }
  val breakevenPrice = maxLossPrice
  val isItm = (undLast > lowerMaxLossPrice) && (undLast < higherMaxLossPrice)
  val isProfitable = isItm
  
  val legsFull = lowerTwoLeg.legsFull + " " + higherTwoLeg.legsFull
  val legsCompact = {
    val low = lowerTwoLeg
    val cOrP = low.callOrPut
    ("L"+low.lowerStrike+cOrP) + (" S(x2)"+low.higherStrike+cOrP) + (" L"+higherTwoLeg.higherStrike+cOrP)
  }
  
  override def toString: String = lowerTwoLeg.toString + " " + higherTwoLeg.toString
  
}

class LongCallButterfly(bullCall: BullCall, bearCall: BearCall) extends LongButterfly(bullCall, bearCall) with Calls
class LongPutButterfly(bullPut: BullPut, bearPut: BearPut) extends LongButterfly(bullPut, bearPut) with Puts