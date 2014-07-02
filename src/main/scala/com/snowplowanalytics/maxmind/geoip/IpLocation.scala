/*
 * Copyright (c) 2012-2013 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.maxmind.geoip

// MaxMind
import com.maxmind.geoip.{
  LookupService,
  Location,
  regionName
}

/**
 * A case class wrapper around the
 * MaxMind Location class.
 */
case class IpLocation(
  countryCode: String,
  countryName: String,
  region: Option[String],
  city: Option[String],
  latitude: Float,
  longitude: Float,
  postalCode: Option[String],
  dmaCode: Option[Int],
  areaCode: Option[Int],
  metroCode: Option[Int],
  regionName: Option[String],
  isp: Option[String] = None
  )

/**
 * Companion class contains a constructor
 * which takes a MaxMind Location class.
 */
object IpLocation {

  // Option-box a MaxMind Int, where MaxMind uses 0 to indicate None
  private val optionify: Int => Option[Int] = i => if (i == 0) None else Some(i)

  /**
   * Constructs an IpLocation from an IP
   * address and MaxMind LookupServices
   */
  def multi(ip: String, maxmind: LookupService, ispService: Option[LookupService]): Option[IpLocation] = {
    Option(maxmind.getLocation(ip)).map(loc => 
      IpLocation(
        countryCode = loc.countryCode,
        countryName = loc.countryName,
        region = Option(loc.region),
        city = Option(loc.city),
        latitude = loc.latitude,
        longitude = loc.longitude,
        postalCode = Option(loc.postalCode),
        dmaCode = optionify(loc.dma_code),
        areaCode = optionify(loc.area_code),
        metroCode = optionify(loc.metro_code),
        regionName = Option(regionName.regionNameByCode(loc.countryCode, loc.region)),
        isp = ispService.map(ls => ls.getOrg(ip)).filter(_ != null)
      )
    )
  }

}
