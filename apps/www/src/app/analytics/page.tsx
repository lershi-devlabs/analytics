"use client"

import * as React from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Label } from "@/components/ui/label"
import { LineChart, Line, CartesianGrid, XAxis, Tooltip, ResponsiveContainer } from "recharts"

// Dummy data for demonstration
const chartData = [
  { date: "May 12", visitors: 15, pageViews: 20, bounceRate: 60 },
  { date: "May 13", visitors: 10, pageViews: 14, bounceRate: 65 },
  { date: "May 14", visitors: 8, pageViews: 12, bounceRate: 70 },
  { date: "May 15", visitors: 3, pageViews: 5, bounceRate: 68 },
  { date: "May 16", visitors: 2, pageViews: 4, bounceRate: 72 },
  { date: "May 17", visitors: 4, pageViews: 7, bounceRate: 66 },
  { date: "May 18", visitors: 5, pageViews: 8, bounceRate: 68 },
]

const pages = [
  { path: "/", visitors: 24 },
  { path: "/post/zenlock-building-a-macos-menu-bar-focus-timer", visitors: 5 },
  { path: "/post/making-your-own-cdn", visitors: 3 },
  { path: "/showcase/ai-learns-to-drive", visitors: 3 },
  { path: "/showcase/arduino-testing", visitors: 2 },
  { path: "/showcase", visitors: 1 },
]

const referrers = [
  { referrer: "linkedin.com", visitors: 7 },
  { referrer: "yandex.ru", visitors: 3 },
  { referrer: "com.linkedin.android", visitors: 1 },
  { referrer: "google.com", visitors: 1 },
]

const countries = [
  { country: "Canada", visitors: 46 },
  { country: "United States of America", visitors: 21 },
  { country: "People's Republic of China", visitors: 14 },
  { country: "Russian Federation", visitors: 11 },
  { country: "India", visitors: 4 },
]

const devices = [
  { device: "Desktop", visitors: 86 },
  { device: "Mobile", visitors: 14 },
]

const os = [
  { os: "Windows", visitors: 75 },
  { os: "iOS", visitors: 11 },
  { os: "GNU/Linux", visitors: 7 },
  { os: "Android", visitors: 4 },
  { os: "Mac", visitors: 4 },
]

export default function AnalyticsPage() {
  return (
    <div className="max-w-6xl mx-auto px-4 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-end md:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Web Analytics</h1>
          <div>www.montek.dev | 0 online</div>
        </div>
        <div className="flex gap-2">
          <div>
            <Label htmlFor="env-select" className="sr-only">Environment</Label>
            <Select>
              <SelectTrigger id="env-select" className="w-[120px]">
                <SelectValue placeholder="Production" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="production">Production</SelectItem>
                <SelectItem value="preview">Preview</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div>
            <Label htmlFor="range-select" className="sr-only">Date Range</Label>
            <Select>
              <SelectTrigger id="range-select" className="w-[120px]">
                <SelectValue placeholder="Last 7 Days" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="7d">Last 7 Days</SelectItem>
                <SelectItem value="30d">Last 30 Days</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      {/* Stats as Tabs */}
      <Tabs defaultValue="visitors" className="w-full">
        <TabsList className="grid grid-cols-3 w-full mb-2 pb-24 bg-zinc-900 border border-zinc-800 rounded-lg">
          <TabsTrigger value="visitors" className="flex flex-col items-start px-6 py-4">
            <span className="text-sm">Visitors</span>
            <span className="text-2xl font-bold">28 <span className="text-xs text-red-500">-58%</span></span>
          </TabsTrigger>
          <TabsTrigger value="pageViews" className="flex flex-col items-start px-6 py-4">
            <span className="text-sm">Page Views</span>
            <span className="text-2xl font-bold">46 <span className="text-xs text-red-500">-80%</span></span>
          </TabsTrigger>
          <TabsTrigger value="bounceRate" className="flex flex-col items-start px-6 py-4">
            <span className="text-sm">Bounce Rate</span>
            <span className="text-2xl font-bold">68% <span className="text-xs text-red-500">+28%</span></span>
          </TabsTrigger>
        </TabsList>
        <TabsContent value="visitors">
          <Card>
            <CardContent>
              <ResponsiveContainer width="100%" height={250}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <Tooltip />
                  <Line type="monotone" dataKey="visitors" stroke="#6366f1" strokeWidth={2} dot={false} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </TabsContent>
        <TabsContent value="pageViews">
          <Card>
            <CardContent>
              <ResponsiveContainer width="100%" height={250}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <Tooltip />
                  <Line type="monotone" dataKey="pageViews" stroke="#4ade80" strokeWidth={2} dot={false} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </TabsContent>
        <TabsContent value="bounceRate">
          <Card>
            <CardContent>
              <ResponsiveContainer width="100%" height={250}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <Tooltip />
                  <Line type="monotone" dataKey="bounceRate" stroke="#ef4444" strokeWidth={2} dot={false} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>

      {/* Pages & Referrers */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card>
          <CardHeader className="pb-2">
            <Tabs defaultValue="pages">
              <TabsList className="mb-2">
                <TabsTrigger value="pages">Pages</TabsTrigger>
                <TabsTrigger value="routes">Routes</TabsTrigger>
                <TabsTrigger value="hostnames">Hostnames</TabsTrigger>
              </TabsList>
              <TabsContent value="pages">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Path</TableHead>
                      <TableHead className="text-right">Visitors</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {pages.map((row) => (
                      <TableRow key={row.path}>
                        <TableCell className="font-mono">{row.path}</TableCell>
                        <TableCell className="text-right font-bold">{row.visitors}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
                <div className="flex justify-end mt-2">
                  <button className="text-xs text-zinc-400 hover:underline">View All</button>
                </div>
              </TabsContent>
              {/* Add content for routes/hostnames as needed */}
            </Tabs>
          </CardHeader>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <Tabs defaultValue="referrers">
              <TabsList className="mb-2">
                <TabsTrigger value="referrers">Referrers</TabsTrigger>
                <TabsTrigger value="utm">UTM Parameters</TabsTrigger>
              </TabsList>
              <TabsContent value="referrers">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Referrer</TableHead>
                      <TableHead className="text-right">Visitors</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {referrers.map((row) => (
                      <TableRow key={row.referrer}>
                        <TableCell>{row.referrer}</TableCell>
                        <TableCell className="text-right font-bold">{row.visitors}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
                <div className="flex justify-end mt-2">
                  <button className="text-xs text-zinc-400 hover:underline">View All</button>
                </div>
              </TabsContent>
              {/* Add content for UTM Parameters as needed */}
            </Tabs>
          </CardHeader>
        </Card>
      </div>

      {/* More breakdowns */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardHeader>
            <CardTitle>Countries</CardTitle>
          </CardHeader>
          <CardContent>
            <table className="w-full text-sm">
              <thead>
                <tr>
                  <th className="text-left">Country</th>
                  <th className="text-right">Visitors</th>
                </tr>
              </thead>
              <tbody>
                {countries.map((row) => (
                  <tr key={row.country}>
                    <td className="py-1">{row.country}</td>
                    <td className="py-1 text-right">{row.visitors}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>Devices</CardTitle>
          </CardHeader>
          <CardContent>
            <table className="w-full text-sm">
              <thead>
                <tr>
                  <th className="text-left">Device</th>
                  <th className="text-right">Visitors</th>
                </tr>
              </thead>
              <tbody>
                {devices.map((row) => (
                  <tr key={row.device}>
                    <td className="py-1">{row.device}</td>
                    <td className="py-1 text-right">{row.visitors}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
        <Card>
          <CardHeader>
            <CardTitle>Operating Systems</CardTitle>
          </CardHeader>
          <CardContent>
            <table className="w-full text-sm">
              <thead>
                <tr>
                  <th className="text-left">OS</th>
                  <th className="text-right">Visitors</th>
                </tr>
              </thead>
              <tbody>
                {os.map((row) => (
                  <tr key={row.os}>
                    <td className="py-1">{row.os}</td>
                    <td className="py-1 text-right">{row.visitors}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}