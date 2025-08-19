package com.adform.adformdemo.support

import com.adform.sdk.network.entities.OpenRTBResponse

fun OpenRTBResponse.toLogString() =
    "OpenRTBResponse(id='$id', bidid='$bidid', cur='$cur', seatbid=${seatbid?.joinToString { it.toLogString() }})"

fun OpenRTBResponse.SeatBid.toLogString() =
    "SeatBid(seat='$seat', group='$group', bid=${bid?.joinToString { it.toLogString() }})"

fun OpenRTBResponse.Bid.toLogString() =
    "Bid(id='$id', impid='$impid', price=$price, nurl='$nurl', adm='$adm', nativeAd=${nativeAd?.toLogString()}, adomain=$adomain, crid='$crid', cat=$cat, dealid='$dealid', w=$w, h=$h, dur=$dur, ext=${ext?.toLogString()})"

fun OpenRTBResponse.NativeAd.toLogString() =
    "NativeAd(ver='$ver', assets=${assets?.joinToString { it.toLogString() }}, asseturl='$asseturl', dcourl='$dcourl', link=${link?.toLogString()}, imptrackers=$imptrackers, jstracker='$jstracker', eventtrackers=${eventtrackers?.joinToString { it.toLogString() }}, privacy='$privacy')"

fun OpenRTBResponse.Asset.toLogString() =
    "Asset(id=$id, required=$required, title=${title?.toLogString()}, data=${data?.toLogString()}, img=${img?.toLogString()}, video=${video?.toLogString()})"

fun OpenRTBResponse.ResponseVideoAsset.toLogString() =
    "ResponseVideoAsset(vasttag='$vasttag')"

fun OpenRTBResponse.Title.toLogString() =
    "Title(text='$text', len=$len)"

fun OpenRTBResponse.Data.toLogString() =
    "Data(type=$type, len=$len, value='$value')"

fun OpenRTBResponse.Image.toLogString() =
    "Image(type=$type, url='$url', w=$w, h=$h)"

fun OpenRTBResponse.Link.toLogString() =
    "Link(url='$url', clicktrackers=$clicktrackers, fallback='$fallback')"

fun OpenRTBResponse.Ext.toLogString() =
    "Ext(prebid=${prebid?.toLogString()}, dsa=${dsa?.toLogString()})"

fun OpenRTBResponse.Prebid.toLogString() =
    "Prebid(type='$type')"

fun OpenRTBResponse.ResponseDsaExt.toLogString() =
    "ResponseDsaExt()"

fun OpenRTBResponse.TrackingEvent.toLogString() =
    "TrackingEvent(event=$event, method=$method, url='$url')"