"use client";

import Link from "next/link";
import HamburgerButton from "@/layouts/hamburger-button";
import SearchWidget from "@/components/search/search";
import Sidebar from "@/layouts/hydrogen/sidebar";
import HeaderMenuRight from "@/layouts/header-menu-right";
import StickyHeader from "@/layouts/sticky-header";
import { Title } from "rizzui";
import IntranetHeaderStrip from "@/components/intranet/intranet-header-strip";

export default function Header({ intranetChrome }: { intranetChrome?: boolean }) {
  return (
    <StickyHeader className="z-[990] 2xl:py-5 3xl:px-8  4xl:px-10">
      <div className="flex min-w-0 flex-1 items-center gap-2">
        <div className="flex min-w-0 max-w-2xl flex-1 items-center">
          <HamburgerButton
            view={
              <Sidebar className="static w-full 2xl:w-full" intranetChrome={intranetChrome} />
            }
          />
          <Link
            href={"/"}
            aria-label="Site Logo"
            className="me-4 shrink-0 text-gray-800 hover:text-gray-900 lg:me-5 xl:hidden"
          >
            <Title>LOGO</Title>
          </Link>

          <SearchWidget />
        </div>
        {intranetChrome ? <IntranetHeaderStrip /> : null}
      </div>

      <HeaderMenuRight intranetChrome={intranetChrome} />
    </StickyHeader>
  );
}
