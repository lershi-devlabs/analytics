"use client";
import { useEffect, useRef, useState } from "react";
import { cn } from "@/lib/utils";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import { Pagination, PaginationContent, PaginationItem } from "@/components/ui/pagination";
import {
  ColumnDef, ColumnFiltersState, FilterFn, PaginationState, Row, SortingState, VisibilityState,
  flexRender, getCoreRowModel, getFacetedUniqueValues, getFilteredRowModel, getPaginationRowModel, getSortedRowModel, useReactTable,
} from "@tanstack/react-table";
import { ChevronDown, ChevronFirst, ChevronLast, ChevronLeft, ChevronRight, ChevronUp, CircleX, ListFilter, Trash, Ellipsis } from "lucide-react";
import { DropdownMenu, DropdownMenuCheckboxItem, DropdownMenuContent, DropdownMenuGroup, DropdownMenuItem, DropdownMenuLabel, DropdownMenuPortal, DropdownMenuSeparator, DropdownMenuShortcut, DropdownMenuSub, DropdownMenuSubContent, DropdownMenuSubTrigger, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface Url {
  id: number;
  originalUrl: string;
  shortUrl: string;
  clickCount: number;
  createdDate: string;
  username: string;
  projectId: string;
}

const urlFilterFn: FilterFn<Url> = (row, columnId, filterValue) => {
  const content = `${row.original.originalUrl} ${row.original.shortUrl}`.toLowerCase();
  return content.includes((filterValue ?? "").toLowerCase());
};
const apiUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

const columns: ColumnDef<Url>[] = [
  {
    id: "select",
    header: ({ table }) => (
      <Checkbox
        checked={table.getIsAllPageRowsSelected() || (table.getIsSomePageRowsSelected() && "indeterminate")}
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label="Select row"
      />
    ),
    size: 28,
    enableSorting: false,
    enableHiding: false,
  },
  {
    header: "Short URL",
    accessorKey: "shortUrl",
    cell: ({ row }) => (
      <a
        href={`${apiUrl}/${row.original.shortUrl}`}
        target="_blank"
        rel="noopener noreferrer"
        className="text-blue-600 underline truncate max-w-xs block"
        style={{ maxWidth: 120 }}
      >
        {row.original.shortUrl}
      </a>
    ),
    size: 120,
    filterFn: urlFilterFn,
    enableHiding: false,
  },
  {
    header: "Original URL",
    accessorKey: "originalUrl",
    cell: ({ row }) => (
      <a
        href={row.original.originalUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="text-blue-600 underline truncate max-w-xs block"
        style={{ maxWidth: 220 }}
      >
        {row.original.originalUrl}
      </a>
    ),
    size: 220,
    filterFn: urlFilterFn,
  },
  {
    header: "Clicks",
    accessorKey: "clickCount",
    size: 80,
  },
  {
    header: "Created",
    accessorKey: "createdDate",
    cell: ({ row }) => new Date(row.original.createdDate).toLocaleString(),
    size: 160,
  },
  {
    header: "Project",
    accessorKey: "projectId",
    cell: ({ row }) => (
      <span className="truncate max-w-xs block" style={{ maxWidth: 180 }}>
        {row.original.projectId}
      </span>
    ),
    size: 180,
  },
  {
    id: "actions",
    header: () => <span className="sr-only">Actions</span>,
    cell: ({ row }) => <RowActions row={row} />,
    size: 60,
    enableHiding: false,
  },
];

export default function MyUrlsPage() {
  const id = "myurls-table";
  const [data, setData] = useState<Url[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [sorting, setSorting] = useState<SortingState>([]);
  const [pagination, setPagination] = useState<PaginationState>({ pageIndex: 0, pageSize: 10 });
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({});
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    async function fetchUrls() {
      setLoading(true);
      setError("");
      try {
        const token = localStorage.getItem("token");
        const res = await fetch(`${apiUrl}/api/urls/myurls`, {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        });
        if (!res.ok) throw new Error("Failed to fetch URLs");
        const urls = await res.json();
        setData(urls);
      } catch {
        setError("Failed to fetch URLs");
      } finally {
        setLoading(false);
      }
    }
    fetchUrls();
  }, []);

  useEffect(() => {
    // Only run on client
    if (typeof window === "undefined") return;

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      client.subscribe("/topic/clicks", (message) => {
        const updatedUrl = JSON.parse(message.body);
        setData((prevData) =>
          prevData.map((url) =>
            url.id === updatedUrl.id
              ? { ...url, clickCount: updatedUrl.clickCount }
              : url
          )
        );
      });
    };

    client.activate();

    return () => {
      client.deactivate();
    };
  }, []);

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
    getFacetedUniqueValues: getFacetedUniqueValues(),
    state: {
      sorting,
      pagination,
      columnFilters,
      columnVisibility,
    },
    onSortingChange: setSorting,
    onPaginationChange: setPagination,
    onColumnFiltersChange: setColumnFilters,
    onColumnVisibilityChange: setColumnVisibility,
  });

  if (loading) return <div className="p-8">Loading...</div>;
  if (error) return <div className="p-8 text-red-600">{error}</div>;

  return (
    <div className="space-y-4 max-w-[1000px] mx-auto">
      <h1 className="text-2xl font-bold mb-4">My Shortened URLs</h1>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-3">
          <div className="relative">
            <Input
              id={`${id}-input`}
              ref={inputRef}
              className={cn("peer min-w-60 ps-9", Boolean(table.getColumn("shortUrl")?.getFilterValue()) && "pe-9")}
              value={(table.getColumn("shortUrl")?.getFilterValue() ?? "") as string}
              onChange={(e) => table.getColumn("shortUrl")?.setFilterValue(e.target.value)}
              placeholder="Filter by URL..."
              type="text"
              aria-label="Filter by URL"
            />
            <div className="pointer-events-none absolute inset-y-0 start-0 flex items-center justify-center ps-3 text-muted-foreground/80 peer-disabled:opacity-50">
              <ListFilter size={16} strokeWidth={2} aria-hidden="true" />
            </div>
            {Boolean(table.getColumn("shortUrl")?.getFilterValue()) && (
              <button
                className="absolute inset-y-0 end-0 flex h-full w-9 items-center justify-center rounded-e-lg text-muted-foreground/80 outline-offset-2 transition-colors hover:text-foreground focus:z-10 focus-visible:outline-2 focus-visible:outline-ring/70 disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50"
                aria-label="Clear filter"
                onClick={() => {
                  table.getColumn("shortUrl")?.setFilterValue("");
                  if (inputRef.current) {
                    inputRef.current.focus();
                  }
                }}
              >
                <CircleX size={16} strokeWidth={2} aria-hidden="true" />
              </button>
            )}
          </div>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline">
                <Ellipsis className="-ms-1 me-2 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />
                View
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuLabel>Toggle columns</DropdownMenuLabel>
              {table
                .getAllColumns()
                .filter((column) => column.getCanHide())
                .map((column) => {
                  return (
                    <DropdownMenuCheckboxItem
                      key={column.id}
                      className="capitalize"
                      checked={column.getIsVisible()}
                      onCheckedChange={(value) => column.toggleVisibility(!!value)}
                      onSelect={(event) => event.preventDefault()}
                    >
                      {column.id}
                    </DropdownMenuCheckboxItem>
                  );
                })}
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
        <div className="flex items-center gap-3">
          {table.getSelectedRowModel().rows.length > 0 && (
            <Button className="ml-auto" variant="outline">
              <Trash className="-ms-1 me-2 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />
              Delete
              <span className="-me-1 ms-3 inline-flex h-5 max-h-full items-center rounded border border-border bg-background px-1 font-[inherit] text-[0.625rem] font-medium text-muted-foreground/70">
                {table.getSelectedRowModel().rows.length}
              </span>
            </Button>
          )}
        </div>
      </div>
      <div className="overflow-hidden rounded-lg border border-border bg-background">
        <Table className="table-fixed">
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id} className="hover:bg-transparent">
                {headerGroup.headers.map((header) => (
                  <TableHead
                    key={header.id}
                    style={{ width: `${header.getSize()}px` }}
                    className="h-11"
                  >
                    {header.isPlaceholder ? null : header.column.getCanSort() ? (
                      <div
                        className={cn(
                          header.column.getCanSort() && "flex h-full cursor-pointer select-none items-center justify-between gap-2",
                        )}
                        onClick={header.column.getToggleSortingHandler()}
                        onKeyDown={(e) => {
                          if (
                            header.column.getCanSort() &&
                            (e.key === "Enter" || e.key === " ")
                          ) {
                            e.preventDefault();
                            header.column.getToggleSortingHandler()?.(e);
                          }
                        }}
                        tabIndex={header.column.getCanSort() ? 0 : undefined}
                      >
                        {flexRender(header.column.columnDef.header, header.getContext())}
                        {{
                          asc: <ChevronUp className="shrink-0 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />,
                          desc: <ChevronDown className="shrink-0 opacity-60" size={16} strokeWidth={2} aria-hidden="true" />,
                        }[header.column.getIsSorted() as string] ?? null}
                      </div>
                    ) : (
                      flexRender(header.column.columnDef.header, header.getContext())
                    )}
                  </TableHead>
                ))}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow key={row.id} data-state={row.getIsSelected() && "selected"}>
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id} className="last:py-0">
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className="h-24 text-center">
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex items-center justify-between gap-8">
        <div className="flex items-center gap-3">
          <Label htmlFor={id} className="max-sm:sr-only">
            Rows per page
          </Label>
          <select
            id={id}
            value={table.getState().pagination.pageSize}
            onChange={(e) => table.setPageSize(Number(e.target.value))}
            className="border rounded px-2 py-1"
          >
            {[5, 10, 25, 50].map((pageSize) => (
              <option key={pageSize} value={pageSize}>
                {pageSize}
              </option>
            ))}
          </select>
        </div>
        <div className="flex grow justify-end whitespace-nowrap text-sm text-muted-foreground">
          <p className="whitespace-nowrap text-sm text-muted-foreground" aria-live="polite">
            <span className="text-foreground">
              {table.getState().pagination.pageIndex * table.getState().pagination.pageSize + 1}-
              {Math.min(
                Math.max(
                  table.getState().pagination.pageIndex * table.getState().pagination.pageSize + table.getState().pagination.pageSize,
                  0,
                ),
                table.getRowCount(),
              )}
            </span>{" "}
            of <span className="text-foreground">{table.getRowCount().toString()}</span>
          </p>
        </div>
        <div>
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <Button size="icon" variant="outline" onClick={() => table.firstPage()} disabled={!table.getCanPreviousPage()} aria-label="Go to first page">
                  <ChevronFirst size={16} strokeWidth={2} aria-hidden="true" />
                </Button>
              </PaginationItem>
              <PaginationItem>
                <Button size="icon" variant="outline" onClick={() => table.previousPage()} disabled={!table.getCanPreviousPage()} aria-label="Go to previous page">
                  <ChevronLeft size={16} strokeWidth={2} aria-hidden="true" />
                </Button>
              </PaginationItem>
              <PaginationItem>
                <Button size="icon" variant="outline" onClick={() => table.nextPage()} disabled={!table.getCanNextPage()} aria-label="Go to next page">
                  <ChevronRight size={16} strokeWidth={2} aria-hidden="true" />
                </Button>
              </PaginationItem>
              <PaginationItem>
                <Button size="icon" variant="outline" onClick={() => table.lastPage()} disabled={!table.getCanNextPage()} aria-label="Go to last page">
                  <ChevronLast size={16} strokeWidth={2} aria-hidden="true" />
                </Button>
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        </div>
      </div>
    </div>
  );
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
function RowActions({ row: _row }: { row: Row<Url> }) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <div className="flex justify-end">
          <Button size="icon" variant="ghost" className="shadow-none" aria-label="Edit item">
            <Ellipsis size={16} strokeWidth={2} aria-hidden="true" />
          </Button>
        </div>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuGroup>
          <DropdownMenuItem>
            <span>Edit</span>
            <DropdownMenuShortcut>⌘E</DropdownMenuShortcut>
          </DropdownMenuItem>
          <DropdownMenuItem>
            <span>Duplicate</span>
            <DropdownMenuShortcut>⌘D</DropdownMenuShortcut>
          </DropdownMenuItem>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          <DropdownMenuItem>
            <span>Archive</span>
            <DropdownMenuShortcut>⌘A</DropdownMenuShortcut>
          </DropdownMenuItem>
          <DropdownMenuSub>
            <DropdownMenuSubTrigger>More</DropdownMenuSubTrigger>
            <DropdownMenuPortal>
              <DropdownMenuSubContent>
                <DropdownMenuItem>Move to project</DropdownMenuItem>
                <DropdownMenuItem>Move to folder</DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem>Advanced options</DropdownMenuItem>
              </DropdownMenuSubContent>
            </DropdownMenuPortal>
          </DropdownMenuSub>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          <DropdownMenuItem>
            <span>Share</span>
            <DropdownMenuShortcut>⌘S</DropdownMenuShortcut>
          </DropdownMenuItem>
          <DropdownMenuItem>
            <span>Add to favorites</span>
            <DropdownMenuShortcut>⌘F</DropdownMenuShortcut>
          </DropdownMenuItem>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuItem className="text-destructive focus:text-destructive">
          <span>Delete</span>
          <DropdownMenuShortcut>⌘⌫</DropdownMenuShortcut>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
} 